package models;

import utilities.Utility;

public abstract class Notification {
	protected int TTL;


	public Notification() {
		this.TTL = Utility.INIT_TTL;
	}

	public void setTTL(int tTL) {
		TTL = tTL;
	}

	public int TTL() {
		return TTL;
	}

	public void decrementTTL() {
		this.TTL--;
	}

	@Override
	public abstract Object clone() throws CloneNotSupportedException;
	
	@Override
	public abstract String toString();
}
