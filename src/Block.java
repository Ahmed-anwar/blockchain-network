import java.util.ArrayList;
import java.util.HashSet;

public class Block {
    HashSet<Transaction> transactions;
    public Block(){
        transactions = new HashSet<Transaction>();
    }

    public void add(Transaction transaction){
        transactions.add(transaction);
    }

    @Override
    public String toString(){
        return transactions.toString();
    }
}
