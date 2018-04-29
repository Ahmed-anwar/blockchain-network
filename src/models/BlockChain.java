package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import utilities.Utility;

public class BlockChain {
	private Block head; // longest chain tail
	private final HashMap<String, Block> cache = new HashMap<String, Block>();
	private final HashMap<String, Integer> chainLen = new HashMap<String, Integer>();
	/**
	 * The full history of received blocks only for testing purposes
	 */
	private final HashMap<String, Block> memory = new HashMap<String, Block>();

	/**
	 * Adds a block to the user's blockchain and updates the head if it creates a longer chain.
	 * @param b the block to add
	 * @return	true if 
	 * @throws Exception
	 */
	public boolean addBlock(Block b) throws Exception {
		 // Invalid transactions signatures or invalid proof of work.
		if (!validateBlock(b))
			return false;
		
		// User has not encountered the previous building block.
		if (b.prevHash() != null && !cache.containsKey(b.prevHash())) 
			return true;
		// Block was received before.
		if(cache.containsKey(b.hash()))
			return true;
		
		// Insert in cache and memory.
		cache.put(b.hash(), b);
		memory.put(b.hash(), b);
		
		// First block to receive ever.
		if (head == null) {
			head = b;
			chainLen.put(b.hash(), 1);
		} 
		else {
			// Store the chain length of this new block.
			int prevLen = chainLen.get(b.prevHash());
			int newLen = prevLen + 1;
			chainLen.put(b.hash(), newLen);
			
			// Update head if it creates a longer chain.s
			int currLen = chainLen.get(head.hash());
			if(newLen > currLen) {
				head = b;
				removeShortChains();
			}
		}

		return true;
	}
	/**
	 * Removes any chain in the cache whose length is less than 
	 * the current longest chain by more than the configured block life. 
	 */
	public void removeShortChains() {
		int currLen = chainLen.get(head.hash());
		ArrayList<Entry<String, Integer>> entries  = new ArrayList<>(chainLen.entrySet());
		for (int i = 0; i < entries.size(); i++) {
			int bLen = entries.get(i).getValue();
			String bHash = entries.get(i).getKey();
			
			if(currLen - bLen > Utility.BLOCK_LIFE) {
				//Remove from cache but NOT memory.
				chainLen.remove(bHash);
				cache.remove(bHash);
			}
		}
	}
	/**
	 * Validates the proof of work and signatures of transactions.
	 * @param b the block to validate
	 * @return	true if the block is valid 
	 */
	public boolean validateBlock(Block b) throws Exception {
		if (!Utility.isTarget(b.hash())) // invalid  mining
			return false;

		for (Transaction transaction : b.transactions())
			// validate transactions
			if (!Utility.verfiySignature(transaction.senderPubKey(), transaction.plainText(), transaction.signature()))
				return false;

		return true;
	}

	public Block head() {
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
