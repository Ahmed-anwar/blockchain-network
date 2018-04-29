package models;

public abstract class Notification {
	private static final int initTTL = 6;
	protected int TTL;

	public Notification() {
		this.TTL = initTTL;
	}

	public Notification(int initTTL) {
		this.TTL = initTTL;
	}

	public int TTL() {
		return TTL;
	}

	public void setTTL(int TTL) {
		this.TTL = TTL;
	}

	@Override
	public abstract Object clone() throws CloneNotSupportedException;
}
