package models;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.UUID;

@SuppressWarnings("serial")
public class Transaction extends Notification implements Serializable, Comparable<Transaction> {
	private String transId, signature;
	private PublicKey senderPubKey;
	private PublicKey receiverPubKey;
	private double val;
	private int orderNum; // order number to be used in TreeSet sorting
	private static int sequence;

	public Transaction(PublicKey senderPubKey, PublicKey receiverPubKey, double val) {
		super();
		this.transId = UUID.randomUUID().toString();
		this.senderPubKey = senderPubKey;
		this.receiverPubKey = receiverPubKey;
		this.val = val;
		this.val = ++sequence;
	}

	@Override
	public int hashCode() {
		return transId.hashCode();
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Override
	public boolean equals(Object obj) {
		Transaction other = (Transaction) obj;
		return transId.equals(other.transId);
	}

	public String transId() {
		return transId;
	}

	public String signature() {
		return signature;
	}

	public PublicKey senderPubKey() {
		return senderPubKey;
	}

	public PublicKey receiverPubKey() {
		return receiverPubKey;
	}

	public double val() {
		return val;
	}

	@Override
	public String toString() {
		return "Transaction [transId=" + transId + ", senderPubKey=" + senderPubKey.hashCode() + ", receiverPubKey="
				+ receiverPubKey.hashCode() + ", val=" + val + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Transaction t = new Transaction(senderPubKey, receiverPubKey, val);
		t.signature = this.signature;
		t.TTL = this.TTL;
		t.transId = this.transId;
		return t;
	}

	@Override
	public int compareTo(Transaction o) {
		return Integer.valueOf(this.orderNum).compareTo(Integer.valueOf(o.orderNum));
	}
}
