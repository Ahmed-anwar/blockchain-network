package main;

import models.Transaction;
import utilities.Utility;
import Network.Network;
import Network.User;

public class Main {

	public static void main(String[] args) throws Exception {
		Network network = new Network();
		final int N = 5;
		// register new N users
		for (int i = 0; i < N; ++i)
			network.registerUser();
		



		System.out.println("\n==================== GENESIS BLOCK ANNOUNCED ========================\n");
		network.announceGenesis();

		User user0 = network.users().get(0);
		User user1 = network.users().get(1);
		for (int i = 0; i < Utility.BLOCK_SIZE << 1; i++) {
			Transaction t = user0.createTransaction(1, user1.pubKey());
			user0.notify(t);
		}
		System.out.print("\n\n");
		for (User user : network.users())
			// print blockchain of each user
			user.printBlockchain();
			System.out.println();
	}
}
