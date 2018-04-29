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
		transactions = new TreeSet<Transaction>();
	}

	// Increases nonce value until hash target is reached.
	public void mineBlock(String prevHash) throws NoSuchAlgorithmException {
		this.prevHash = prevHash;
		String merkleRoot = getMerkleRoot();
		String target = new String(new char[Utility.DIFFICULITY]).replace('\0', '0');
		hash = calcHash(merkleRoot);
		int nonce = 0;
		while (!hash.substring(0, Utility.DIFFICULITY).equals(target)) {
			nonce++;
			hash = calcHash(merkleRoot + nonce);
		}
		System.out.println("Block Mined!!! : " + hash);
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
	public void add(Transaction transaction) throws Exception {
		if (Utility.verfiySignature(transaction.senderPubKey(), transaction.toString(), transaction.signature()))
			transactions.add(transaction);
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
		return "Block [hash=" + hash + ", prevHash=" + prevHash + ", transactions=" + transactions + "]";
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
		this.transactions.forEach(t -> { b.transactions.add(t);});
		return b;
	}

	public int size() {
		return transactions.size();
	}

}
