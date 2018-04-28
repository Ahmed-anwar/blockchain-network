import java.util.HashSet;

public class Block {
	HashSet<Transaction> transactions;
	public Block(){
		transactions = new HashSet<Transaction>();
	}

	/**
	 * Adds a new transaction to a block (duplicates are removed).
	 * @param transaction the transaction to add to the block
	 */
	public void add(Transaction transaction){
		transactions.add(transaction);
	}

	@Override
	public String toString(){
		return transactions.toString();
	}
}
