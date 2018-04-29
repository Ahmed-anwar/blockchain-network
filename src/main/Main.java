package main;

import models.Transaction;
import utilities.Utility;
import Network.Network;
import Network.User;

public class Main {

	static Network network;
	public static void main(String[] args) throws Exception {
		network = new Network();
		
		final int N = 5;
		// register new N users
		for (int i = 0; i < N; ++i)
			network.registerUser();
		
		network.announceGenesis();

		test1();
	}
	public static void test1() throws Exception {
		User user0 = network.users().get(0);
		User user1 = network.users().get(1);
		for (int i = 0; i < Utility.BLOCK_SIZE << 1; i++) {
			Transaction t = user0.createTransaction(1, user1.pubKey());
			user0.notify(t);
		}
		System.out.print("\n\n");
		for (User user : network.users())
			user.printBlockchain();
			System.out.println();
	}
}
