package models;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TreeSet;

import utilities.Utility;
import models.Transaction;

public class Block extends Notification {
	private String hash, prevHash;
	private TreeSet<Transaction> transactions;

	public Block() {
		super();
		transactions = new TreeSet<Transaction>();
	}

	// Increases nonce value until hash target is reached.
	public void mineBlock(String prevHash) throws NoSuchAlgorithmException {
		this.prevHash = prevHash;
		String merkleRoot = getMerkleRoot();
		hash = calcHash(merkleRoot);
		int nonce = 0;
		while (!Utility.isTarget(hash)) {
			nonce++;
			hash = calcHash(merkleRoot + nonce);
		}
	}

	private String calcHash(String s) throws NoSuchAlgorithmException {
		return Utility.hash(s);
	}

	// Tacks in array of transactions and returns a merkle root.
	private String getMerkleRoot() throws NoSuchAlgorithmException {
		int count = transactions.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for (Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.transId());
		}
		ArrayList<String> treeLayer = previousTreeLayer;
		while (count > 1) {
			treeLayer = new ArrayList<String>();
			for (int i = 1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(Utility.hash(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}

	/**
	 * Adds a new transaction to a block (duplicates are removed).
	 *
	 * @param transaction
	 *            the transaction to add to the block
	 * @throws Exception
	 */
	public boolean add(Transaction transaction) throws Exception {
		if (Utility.verfiySignature(transaction.senderPubKey(), transaction.toString(), transaction.signature())) {
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
		return "Block " + hash.substring(0, 5);
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
