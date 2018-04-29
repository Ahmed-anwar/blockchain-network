package network;

import java.security.*;
import java.util.ArrayList;
import java.util.Random;

import models.Block;
import models.BlockChain;
import models.Message;
import models.Transaction;
import utilities.Utility;

public class User {
	public int userId;
	private KeyPair myKeyPair;
	private ArrayList<User> peers;
	private BlockChain blockChain;
	private Block currBlock;

	private static int sequence = -1;

	/**
	 * Constructs a new user with the given pair of keys.
	 *
	 * @param keyPair
	 *            the pair of public and private keys
	 */
	public User(KeyPair keyPair) throws NoSuchAlgorithmException {
		this.myKeyPair = keyPair;
		this.peers = new ArrayList<>();
		this.currBlock = new Block();
		this.blockChain = new BlockChain();
		this.userId = sequence++;
	}


	/**
	 * Creates and signs a new transaction from the current user to some recipient.
	 * @param val the amount of bitcoins to transfer
	 * @param receiverPubKey the public key of the receiver
	 * @return the constructed transaction
	 */
	public Transaction createTransaction(double val, PublicKey receiverPubKey) throws Exception {

		// Create the transaction.
		Transaction transaction = new Transaction(myKeyPair.getPublic(), receiverPubKey, val);
		// sign transaction
		transaction.setSignature(Utility.generateSignature(myKeyPair.getPrivate(), transaction.plainText()));
		return transaction;
	}

	/**
	 * 
	 * Processes the received message & notifies a random set of my peers, who in turn notify their peers until
	 * the TTL of the message runs out.
	 * @param msg the message to notify: either a block or a transaction
	 */
	public void notify(Message msg) throws Exception {
		if (msg instanceof Transaction) {	//transaction
			Transaction trans = (Transaction) msg;
			if(!currBlock.add(trans)) //invalid signature
				return;

			printMessage(msg);

			// Check if my current block is ready to mine.
			Block b = tryMining(); 
			if(b != null)	//mining was successful
				notify(b);

		} else { // block
			if (!blockChain.addBlock((Block) msg)) // an invalid block
				return;

			printMessage(msg);
			
			// Remove intersection between received block and my current mining block if any.
			currBlock.removeIntersection((Block) msg);
		}
		notifyPeers(msg);
	}
	/**
	 *  Mines the user's current block if it reached its capacity, and flushes it afterwards.
	 * @return the mined block (null if failed)
	 * @throws Exception
	 */
	public Block tryMining() throws Exception {
		if (currBlock.size() == Utility.BLOCK_SIZE) {
			currBlock.mineBlock(blockChain.head().hash());
			System.out.printf("\nUser %d mined block %s with trans\n %s\n", userId, currBlock.toString(), currBlock.transactions().toString());
			Block b = (Block) currBlock.clone();
			blockChain.addBlock(b);
			b = (Block) currBlock.clone();
			currBlock.flush();
			return b;
		}
		return null;
	}
	/**
	 * Notifies a random percentage of my peers with a message.
	 * Guarantees the notification of at least one of my peers.
	 * @param msg the message to propagate
	 */
	public void notifyPeers(Message msg) throws Exception {
		// Decrement the time to live (TTL) of the msg.
		msg.decrementTTL();
		
		// Drop the message if its TTL reached zero.
		if(msg.TTL() <= 0)
			return;
		
		// Prepare a clone of the msg to pass on.
		Message passed = (Message) msg.clone();
		
		// Choose a random peer to deterministically notify.
		int seed = (new Random()).nextInt(peers.size());
		peers.get(seed).notify(passed);

		// Choose a random percentage (between 5% and 20%) of my peers to notify.
		int percentageOfNotifiedPeers = new Random().nextInt(15) + 5;
		for (User peer : peers) {
			int send = new Random().nextInt(percentageOfNotifiedPeers);
			if (send == 0) {
				passed = (Message) msg.clone();
				peer.notify(passed);
			}

		}
	}
	/**
	 * Notifies all of my peers deterministically with a TTL of 1.
	 * @param msg
	 * @throws CloneNotSupportedException
	 * @throws Exception
	 */
	public void notifiyAll(Message msg) throws CloneNotSupportedException, Exception {
		msg.setTTL(1); // set ttl to 1 to prevent back propagation of
		// this msg
		for (User peer : peers)
			peer.notify((Message) msg.clone());
	}

	/**
	 * Prints a received message.
	 * @param msg	the message the user received
	 */
	private void printMessage(Message msg) {
		if (msg instanceof Transaction) {
			System.out.print("\nUser " + userId + " added transaction: ");
			System.out.println(((Transaction) msg).transId());
		}

		else{
			System.out.print("\nUser " + userId + " added block : ");
			System.out.println((Block) msg);
		}
	}

	/**
	 * Creates a link between another user and me.
	 *
	 * @param user
	 *            my peer
	 */
	public void addPeer(User user) {
		peers.add(user);
	}

	/**
	 * Prints the list of transactions accumulated in my block so far.
	 */
	public void printBlockchain() {
		System.out
		.print("User " + userId + " with pubKey = " + myKeyPair.getPublic().hashCode() + " has blockchain : ");
		System.out.println(blockChain);
	}

	/**
	 * Prints the list of my peers as IDs.
	 */
	public void printMyPeers() {
		System.out.print("User " + userId + " with pubKey = " + myKeyPair.getPublic().hashCode() + " has peers : ");
		System.out.println(peers.toString());
	}

	public String toString() {
		return myKeyPair.getPublic().hashCode() + "";
	}

	public Block currBlock() {
		return currBlock;
	}

	public PublicKey pubKey() {
		return myKeyPair.getPublic();
	}
}
