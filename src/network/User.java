package network;

import java.security.*;
import java.util.ArrayList;
import java.util.Random;

import models.Block;
import models.BlockChain;
import models.Notification;
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
	 * Creates a new transaction from the given message and notifies some of my
	 * peers.
	 *
	 * @param message
	 *            the actual content of the transaction
	 * @throws Exception
	 */
	public Transaction createTransaction(double val, PublicKey receiverPubKey) throws Exception {

		// Create the transaction.
		Transaction transaction = new Transaction(myKeyPair.getPublic(), receiverPubKey, val);
		// sign transaction
		transaction.setSignature(Utility.generateSignature(myKeyPair.getPrivate(), transaction.plainText()));
		return transaction;
	}

	/**
	 * Notifies a random set of my peers, who in turn notify their peers until
	 * the TTL of the notification runs out.
	 *
	 * @param ntfc
	 *            the transaction encapsulated with the signature of the
	 *            original sender and the TTL
	 * @throws Exception
	 */
	public void notify(Notification ntfc) throws Exception {
		// Add the transaction to my block.
		if (ntfc instanceof Transaction) {
			Transaction trans = (Transaction) ntfc;
			if(!currBlock.add(trans))
				return;
			printAnnouncement(ntfc);
			Block b = tryMining();
			if(b != null)
				notify(b);

		} else {
			boolean added = blockChain.addBlock((Block) ntfc);
			if (!added) // not a valid block
				return;
			printAnnouncement(ntfc);
			currBlock.removeIntersection((Block) ntfc);
		}

		notifyPeers(ntfc);

	}
	public Block tryMining() throws Exception {
		// Announce block if it reached maximum size and was mined correctly.
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
	public void notifyPeers(Notification notification) throws Exception {
		// Decrement the time to live (TTL) of the notification.
		notification.decrementTTL();
		if(notification.TTL() == 0)
			return;
		// Prepare a clone of the notification to pass on.
		Notification passed = (Notification) notification.clone();
		// Choose a random peer to deterministically notify.
		int seed = (new Random()).nextInt(peers.size());
		peers.get(seed).notify(passed);

		// Choose a random percentage (between 5% and 20%) of my peers to
		// notify.
		int percentageOfNotifiedPeers = new Random().nextInt(15) + 5;
		for (User peer : peers) {
			int send = new Random().nextInt(percentageOfNotifiedPeers);
			if (send == 0) {
				passed = (Notification) notification.clone();
				peer.notify(passed);
			}

		}
	}
	public void notifiyAll(Notification notification) throws CloneNotSupportedException, Exception {
		notification.setTTL(0); // set ttl to 0 to prevent back propagation of
		// this notification
		for (User peer : peers)
			peer.notify((Notification) notification.clone());
	}

	private void printAnnouncement(Notification notification) {
		if (notification instanceof Transaction) {
			System.out.print("\nUser " + userId + " added transaction: ");
			System.out.println(((Transaction) notification).transId());
		}

		else{
			System.out.print("\nUser " + userId + " added block : ");
			System.out.println((Block) notification);
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
