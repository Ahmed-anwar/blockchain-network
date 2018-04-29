package main;

import models.Block;
import models.Transaction;
import network.Network;
import network.User;
import utilities.Utility;

public class Main {

	static Network network;
	public static void main(String[] args) throws Exception {
		network = new Network();
		final int N = 5;

		// Register new N users.
		for (int i = 0; i < N; ++i)
			network.registerUser();
		// Announce the genesis block.
		network.announceGenesis();

		// Test the longest chain scenario.
		test2();
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
	public static void test2() throws Exception {
		User user0 = network.users().get(0);
		User user1 = network.users().get(1);
		User user2 = network.users().get(2);
		for (int i = 0; i < Utility.BLOCK_SIZE; i++) {
			Transaction t = user0.createTransaction(1, user1.pubKey());
			user0.currBlock().add(t);
			user1.currBlock().add(t);
		}
		Block b1 = user0.tryMining();
		System.out.println("User 0 mined the block " + b1.toString());
		Block b1Prime = user1.tryMining();
		System.out.println("User 1 mined the block " + b1Prime.toString());
		user2.notify(b1);
		System.out.println("User 2 received the block " + b1.toString());
		user2.notify(b1Prime);
		System.out.println("User 2 received the block " + b1Prime.toString());

		for (int i = 0; i < Utility.BLOCK_SIZE; i++) {
			Transaction t = user0.createTransaction(1, user1.pubKey());
			user0.currBlock().add(t);
		}
		Block b2 = user0.tryMining();
		System.out.println("User 0 mined the block " + b2.toString());
		user2.notify(b2);
		System.out.println("User 2 received the block " + b2.toString());

		System.out.println("========User 2 blockchain======");
		user2.printBlockchain();

	}
}
