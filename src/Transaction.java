import java.io.Serializable;
import java.util.UUID;

public class Transaction implements Serializable {
    private String transId;
    private String senderId;
    private String message;

    public Transaction(String senderId, String message){
        transId = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.message = message;
    }

    public String getTransId() {
        return transId;
    }

    public String getSenderId() {
        return senderId;
    }

    @Override
    public String toString(){
        return message;
    }

    @Override
    public int hashCode() {
        return transId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Transaction other = (Transaction) obj;
        return transId.equals(other.transId);
    }
}
