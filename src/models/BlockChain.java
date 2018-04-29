package models;

import java.util.HashMap;

import utilities.Utility;

public class BlockChain {
	Block tail; // longest chain tail

	final HashMap<String, Block> cache = new HashMap<String, Block>();
	final HashMap<String, Integer> chainLen = new HashMap<String, Integer>();

	public boolean addBlock(Block b) throws Exception {
		if (cache.containsKey(b.hash())) // received block before
			return false;

		if (!validateBlock(b))
			return false;

		cache.put(b.hash(), b);
		if (tail == null) {
			tail = b;
			chainLen.put(b.hash(), 1);
		} else {
			int prevLen = chainLen.get(b.prevHash());
			chainLen.put(b.hash(), prevLen++);
			int currLen = chainLen.get(tail.hash());
			tail = (currLen >= prevLen) ? tail : b;
		}

		return true;
	}

	public boolean validateBlock(Block b) throws Exception {
		if (b.prevHash() != null && !cache.containsKey(b.prevHash())) // prevHash
																		// not
																		// present
			return false;

		String target = new String(new char[Utility.DIFFICULITY]).replaceAll("\0", "0");
		if (!b.hash().substring(0, Utility.DIFFICULITY).equals(target)) // invalid
																		// mining
			return false;

		for (Transaction transaction : b.transactions())
			// validate transcations
			if (!Utility.verfiySignature(transaction.senderPubKey(), transaction.toString(), transaction.signature()))
				return false;

		return true;
	}

	public Block tail() {
		return tail;
	}

	@Override
	public String toString() {
		return "BlockChain [tail=" + tail + ", \n numBlocks = " + cache.size() + ", cache=" + cache + "]";
	}

}
