package Network;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import models.Block;
import models.Transaction;
import utilities.Utility;

public class Network {

	private ArrayList<User> users = new ArrayList<>();

	/* Coin base is not part of the network it is responsible for initiating
	the first transaction. */
	private User coinbase;

	public Network() throws NoSuchAlgorithmException {
		// A dummy unregistered user.
		coinbase = new User(Utility.generateKeyPair());
	}
	public void announceGenesis() throws Exception {
		/* Create a genesis block and notifiy all users about it */
		Block genesisBlock = new Block();
		for (int i = 0; i < Utility.BLOCK_SIZE; i++) {
			// A dummy transaction from the coinbase user to itself.
			Transaction genesisTransaction = coinbase.createTransaction(0, coinbase.pubKey());
			genesisBlock.add(genesisTransaction);
		}
		genesisBlock.mineBlock(null); // no prevHash
		coinbase.notifiyAll(genesisBlock);
	}
	/**
	 * Registers a new user into the network and connects it with at least one
	 * other peer.
	 */
	public void registerUser() throws NoSuchAlgorithmException {
		// Create a new user with a given key pair.
		User newUser = new User(Utility.generateKeyPair());
		// Add a connection from coinbase to allow the first transaction to reach all users in network
		coinbase.addPeer(newUser);

		// If it's the first user in the network, add it right away.
		if (users.isEmpty()) {
			users.add(newUser);
			return;
		}

		// Randomly choose an existing user to guarantee a connection with it.
		int seed = (new Random()).nextInt(users.size());
		User seedUser = users.get(seed);
		newUser.addPeer(seedUser);
		seedUser.addPeer(newUser);

		// Choose a random probability (between 5% and 20%) to connect to a
		// given existing user.
		int percentageOfEdges = new Random().nextInt(15) + 5;
		for (int i = 0; i < users.size(); i++) {
			int edge = new Random().nextInt(percentageOfEdges);
			if (edge == 0 && i != seed) {
				User peer = users.get(i);
				newUser.addPeer(peer);
				peer.addPeer(newUser);
			}
		}
		// Add the new user to the network.
		users.add(newUser);
	}

	public ArrayList<User> users() {
		return users;
	}

	public User coinbase() {
		return coinbase;
	}

	/**
	 * Prints the existing user IDs along with the peers and current block of
	 * each.
	 */
	// public void print() {
	// int counter = 1;
	// for (User u : users){
	// System.out.println("=========================================================================");
	// System.out.println("=============================== User #" + counter++ +
	// " ===============================");
	// System.out.println("=========================================================================");
	// System.out.println("User ID: " + u);
	// System.out.println("Peers: " + u.peers.toString());
	// System.out.println("Block: " + u.getBlock());
	// System.out.println(" ");
	// }
	// }
	// /**
	// * Prints the existing user IDs along with the current block of each.
	// */
	// public void printUsersBlocks(){
	// for (User user: users)
	// user.printMyBlocks();
	// }
	// /**
	// * Prints the existing user IDs along with the peers of each.
	// */
	// public void printUsersPeers() {
	// for (User user: users)
	// user.printMyPeers();
	// }

}
