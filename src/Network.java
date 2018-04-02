import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

public class Network {

	ArrayList<User> users = new ArrayList<>();

	/**
	 * Registers a new user into the network and connects it with at least one other peer.
	 */
	public void registerUser() throws NoSuchAlgorithmException {
		// Create a new user with a given key pair.
		User newUser = new User(Utility.generateKeyPair());

		// If it's the first user in the network, add it right away.
		if(users.isEmpty()){
			users.add(newUser);
			return;
		}

		// Randomly choose an existing user to guarantee a connection with it.
		int seed = (new Random()).nextInt(users.size());
		User seedUser = users.get(seed);
		newUser.addPeer(seedUser);
		seedUser.addPeer(newUser);

		// Choose a random probability (between 5% and 20%) to connect to a given existing user. 
		int percentageOfEdges = new Random().nextInt(15) + 5;
		for (int i = 0; i < users.size(); i++) {
			int edge = new Random().nextInt(percentageOfEdges);
			if(edge == 0 && i != seed){
				User peer = users.get(i);
				newUser.addPeer(peer);
				peer.addPeer(newUser);
			}
		}
		// Add the new user to the network.
		users.add(newUser);
	}


	/**
	 * Prints the existing user IDs along with the peers and current block of each.
	 */
	public void print() {
		int counter = 1;
		for (User u : users){
			System.out.println("=========================================================================");
			System.out.println("=============================== User #" + counter++ + " ===============================");
			System.out.println("=========================================================================");
			System.out.println("User ID: " + u);
			System.out.println("Peers: " + u.peers.toString());
			System.out.println("Block: " + u.getBlock());
			System.out.println(" ");
		}
	}
	/**
	 * Prints the existing user IDs along with the current block of each.
	 */
	public void printUsersBlocks(){
		for (User user: users)
			user.printMyBlocks();
	}
	/**
	 * Prints the existing user IDs along with the peers of each.
	 */
	public void printUsersPeers() {
		for (User user: users)
			user.printMyPeers();
	}


}
