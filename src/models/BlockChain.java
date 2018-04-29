package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
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
			int newLen = prevLen + 1;
			chainLen.put(b.hash(), newLen);
			int currLen = chainLen.get(tail.hash());
			if(newLen > currLen) {
				tail = b;
				removeShortChains();
			}
		}

		return true;
	}
	public void removeShortChains() {
		ArrayList<Entry<String, Integer>> entries  = new ArrayList<>(chainLen.entrySet());
		for (int i = 0; i < entries.size(); i++) {
			int bLen = entries.get(i).getValue();
			String bHash = entries.get(i).getKey();
			int currLen = chainLen.get(tail.hash());
			if(currLen - bLen > Utility.BLOCK_LIFE) {
				chainLen.remove(bHash);
				cache.remove(bHash);
			}
		}
	}
	public boolean validateBlock(Block b) throws Exception {
		if (b.prevHash() != null && !cache.containsKey(b.prevHash())) // prevHash not present
			return false;

		if (!Utility.isTarget(b.hash())) // invalid  mining
			return false;

		for (Transaction transaction : b.transactions())
			// validate transactions
			if (!Utility.verfiySignature(transaction.senderPubKey(), transaction.plainText(), transaction.signature()))
				return false;

		return true;
	}

	public Block tail() {
		return tail;
	}

	@Override
	public String toString() {
		return "tail= " + tail + "\n chain length = " + chainLen.get(tail.hash()) + "\n cache= " + cache.values().toString();
	}

}
