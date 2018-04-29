package main;

import java.util.Random;
import models.Block;
import models.Transaction;
import Network.Network;
import Network.User;

public class Main {

	public static void main(String[] args) throws Exception {
		Network network = new Network();
		User coinbase = network.coinbase();
		final int N = 5;
		// register new N users
		for (int i = 0; i < N; ++i)
			network.registerUser();

		System.out.println();
		System.out.println("==================== PEERS ========================");
		System.out.println();

		// print peers connections
		for (User user : network.users())
			user.printMyPeers();

		// create genesis transaction by coinbase user and notifiy all peers in
		// the network
		int randomIdx = new Random().nextInt(N);
		Transaction genesisTransaction = coinbase.createTransaction(100, network.users().get(randomIdx).pubKey());
		coinbase.notifiyAll(genesisTransaction);

		// create a genesis block and notifiy all user about it
		Block genesisBlock = new Block();
		genesisBlock.add(genesisTransaction);
		genesisBlock.mineBlock(null); // no prevHash
		coinbase.notifiyAll(genesisBlock);

		System.out.println();
		System.out.println("==================== GENESIS BLOCK ANNOUNCED ========================");
		System.out.println();

		// print blockchain of each user
		for (User user : network.users())
			user.printBlockchain();

		System.out.println();
		System.out.println("==================== NEW TRANSACTION ANNOUNCED ========================");
		System.out.println();
		
		/*
		 * for this test, I setted the BLOCK_SIZE in utilities class to 1 to
		 * check that a block is created at some users
		 */
		User user1 = network.users().get(0);
		User user2 = network.users().get(1);
		Transaction t = user1.createTransaction(2000, user2.pubKey());
		user1.notify(t);

		for (User user : network.users())
			// print blockchain of each user
			user.printBlockchain();
	}
}
