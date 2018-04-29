package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import utilities.Utility;

public class BlockChain {
	Block head; // longest chain tail
	final HashMap<String, Block> cache = new HashMap<String, Block>();
	final HashMap<String, Block> memory = new HashMap<String, Block>(); //full history
	final HashMap<String, Integer> chainLen = new HashMap<String, Integer>();

	public boolean addBlock(Block b) throws Exception {
		if (cache.containsKey(b.hash()) || !validateBlock(b)) // received block before
			return false;
		
		cache.put(b.hash(), b);
		memory.put(b.hash(), b);

		if (head == null) {
			head = b;
			chainLen.put(b.hash(), 1);
		} 
		else {
			int prevLen = chainLen.get(b.prevHash());
			int newLen = prevLen + 1;
			chainLen.put(b.hash(), newLen);
			int currLen = chainLen.get(head.hash());
			if(newLen > currLen) {
				head = b;
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
			int currLen = chainLen.get(head.hash());
			if(currLen - bLen > Utility.BLOCK_LIFE) {
				//Remove from cache but NOT memory.
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
		return head;
	}

	@Override
	public String toString() {
		return "\nchain length = " + chainLen.get(head.hash())
				+ "\nchain = " + currChain().toString()
				+ "\ncache= " + cache.values().toString();
	}
	
	public LinkedList<Block> currChain(){
		LinkedList<Block> chain = new LinkedList<Block>();
		Block b = head;
		while(b != null) {
			chain.add(b);
			b = memory.get(b.prevHash());
		}
		return chain;
	}

}
