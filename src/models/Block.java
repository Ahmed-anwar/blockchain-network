package models;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TreeSet;

import utilities.Utility;
import models.Transaction;

public class Block extends Message {
	private String hash, prevHash;
	private TreeSet<Transaction> transactions;

	public Block() {
		super();
		transactions = new TreeSet<Transaction>();
	}

	
	/**
	 * Hashes the current block using its hashed Merkle tree, a timestamp and 
	 * a nonce that achieves the proof of work.
	 * @param prevHash the hash of the previous block we build upon
	 */
	public void mineBlock(String prevHash) throws NoSuchAlgorithmException {
		this.prevHash = prevHash;
		String merkleRoot = merkleRoot();
		long timestamp = System.currentTimeMillis();
		merkleRoot += "" + timestamp;
		hash = Utility.hash(merkleRoot);
		
		//Try nonce values sequentially until you hit the target.
		int nonce = 0;
		while (!Utility.isTarget(hash)) {
			nonce++;
			hash = Utility.hash(merkleRoot + nonce);
		}
	}

	/**
	 * Constructs the Merkle tree of the hashes of the block transactions.
	 * @return the hashed Merkle root
	 */
	private String merkleRoot() throws NoSuchAlgorithmException {
		int count = transactions.size();
		ArrayList<String> prevTreeLayer = new ArrayList<String>();
		for (Transaction transaction : transactions) {
			prevTreeLayer.add(transaction.transId());
		}
		ArrayList<String> treeLayer = prevTreeLayer;
		while (count > 1) {
			treeLayer = new ArrayList<String>();
			for (int i = 1; i < prevTreeLayer.size(); i++) {
				treeLayer.add(Utility.hash(prevTreeLayer.get(i - 1) + prevTreeLayer.get(i)));
			}
			count = treeLayer.size();
			prevTreeLayer = treeLayer;
		}
		return treeLayer.get(0);
	}

	/**
	 * Adds a new transaction to a block (duplicates are removed).
	 *
	 * @param transaction
	 *            the transaction to add to the block
	 * @return true if the signature of the transaction is verified
	 */
	public boolean add(Transaction transaction) throws Exception {
		if (	Utility.verfiySignature(transaction.senderPubKey(), transaction.plainText(), transaction.signature())) {
			transactions.add(transaction);
			return true;
		}
		return false;
	}

	public String hash() {
		return hash;
	}

	public String prevHash() {
		return prevHash;
	}

	public TreeSet<Transaction> transactions() {
		return transactions;
	}

	@Override
	public String toString() {
		return hash.substring(0, 5);
	}

	@Override
	public int hashCode() {
		return hash.hashCode();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Block b = new Block();
		b.hash = this.hash;
		b.prevHash = this.prevHash;
		b.TTL = this.TTL;
		b.transactions = new TreeSet<Transaction>();
		this.transactions.forEach(t -> {
			b.transactions.add(t);
		});
		return b;
	}

	public int size() {
		return transactions.size();
	}

	public void flush() {
		transactions = new TreeSet<>();
		hash = null; prevHash = null;
	}

	public void removeIntersection(Block b) {
		this.transactions.removeAll(b.transactions);
	}
}
