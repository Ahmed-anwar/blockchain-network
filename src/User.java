import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class User {
	private String id;
	private KeyPair myKeyPair;
	public ArrayList<User> peers;
	private Block block;
	@SuppressWarnings("unused")
	private BlockChain blockChain;

	/**
	 * Constructs a new user with the given pair of keys.
	 * 
	 * @param keyPair the pair of public and private keys
	 */
	public User(KeyPair keyPair) throws NoSuchAlgorithmException {
		id = UUID.randomUUID().toString();
		myKeyPair = keyPair;
		peers = new ArrayList<>();
		block = new Block();
		blockChain = new BlockChain();
	}

	/**
	 * Creates a new transaction from the given message and notifies some of my peers.
	 * 
	 * @param message the actual content of the transaction
	 */
	public void createTransaction(String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, CloneNotSupportedException, IOException {
		// Create the transaction.
		Transaction transaction = new Transaction(id, message);
		// Encapsulate it in a notification.
		Notification notification = new Notification(transaction);
		// Sign the contents of the notification.
		notification.setSignature(Utility.generateSignature(myKeyPair.getPrivate(), transaction));
		// Announce the transaction.
		notify(notification);
	}

	/**
	 * Notifies a random set of my peers, who in turn notify their peers 
	 * until the TTL of the notification runs out.
	 * 
	 * @param notification the transaction encapsulated with the signature
	 * of the original sender and the TTL
	 */
	public void notify(Notification notification) throws CloneNotSupportedException {
		// Add the transaction to my block.
		block.add(notification.transaction);

		// Drop the transaction if it has reached its hop limit.
		if(notification.TTL == 0)
			return;

		// Decrement the time to live (TTL) of the notification.
		notification.TTL--;

		// Prepare a clone of the notification to pass on.
		Notification passed = (Notification) notification.clone();

		// Choose a random peer to deterministically notify.
		int seed = (new Random()).nextInt(peers.size());
		peers.get(seed).notify(passed);

		// Choose a random percentage (between 5% and 20%) of my peers to notify.
		int percentageOfNotifiedPeers = new Random().nextInt(15) + 5;
		for (User peer : peers) {
			int send = new Random().nextInt(percentageOfNotifiedPeers);
			if(send == 0) {
				passed = (Notification) notification.clone();
				peer.notify(passed);
			}

		}
	}

	/**
	 * Creates a link between another user and me.
	 * @param user my peer
	 */
	public void addPeer(User user){
		peers.add(user);
	}

	/**
	 * Prints the list of transactions accumulated in my block so far.
	 */
	public void printMyBlocks(){
		System.out.print(id + ": ");
		System.out.println(block);
	}

	/**
	 * Prints the list of my peers as IDs.
	 */
	public void printMyPeers() {
		System.out.print(id + ": ");
		System.out.println(peers.toString());
	}

	public String toString(){
		return id;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
}
