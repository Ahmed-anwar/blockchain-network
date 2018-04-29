package Network;

import java.security.*;
import java.util.ArrayList;
import java.util.Random;

import models.Block;
import models.BlockChain;
import models.Notification;
import models.Transaction;
import utilities.Utility;

public class User {
	private int userId;
	private KeyPair myKeyPair;
	private ArrayList<User> peers;
	private BlockChain blockChain;
	private Block currBlock;

	private static int sequence;

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
		transaction.setSignature(Utility.generateSignature(myKeyPair.getPrivate(), transaction.toString()));
		// Encapsulate transaction in a notification.
		return transaction;
	}

	/**
	 * Notifies a random set of my peers, who in turn notify their peers until
	 * the TTL of the notification runs out.
	 * 
	 * @param notification
	 *            the transaction encapsulated with the signature of the
	 *            original sender and the TTL
	 * @throws Exception
	 */
	public void notify(Notification notification) throws Exception {
		// Add the transaction to my block.
		if (notification instanceof Transaction) {
			Transaction transaction = (Transaction) notification;
			boolean isValid = Utility.verfiySignature(transaction.senderPubKey(), transaction.toString(),
					transaction.signature());
			if (!isValid)
				return;

			currBlock.add(transaction);

		} else {
			boolean added = blockChain.addBlock((Block) notification);
			if (!added) // not a valid block
				return;
		}
		printAnnouncement(notification);

		// Drop the notfication if it has reached its hop limit.
		if (notification.TTL() == 0)
			return;

		// Decrement the time to live (TTL) of the notification.
		notification.setTTL(notification.TTL() - 1);

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

		// announce block if it reached maximum size and is mined correctly
		if (currBlock.size() == Utility.BLOCK_SIZE) {
			currBlock.mineBlock(blockChain.tail().hash());
			String target = new String(new char[Utility.DIFFICULITY]).replaceAll("\0", "0");
			if (currBlock.hash().substring(0, Utility.DIFFICULITY).equals(target))
				notify(currBlock);
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
			System.out.print("User " + userId + " with pubKey = " + myKeyPair.getPublic().hashCode()
					+ " added new transaction : ");
			System.out.println((Transaction) notification);
			return;
		}

		if (notification instanceof Block) {
			System.out.print("User " + userId + " with pubKey = " + myKeyPair.getPublic().hashCode()
					+ " added new block : ");
			System.out.println((Block) notification);
			return;
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
