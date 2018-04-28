import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public class Transaction implements Serializable {
	private String transId;
	private String senderId;
	private String message;

	/** Constructs a transaction with the specified message and sender.
	 * @param senderId the id of the sender
	 * @param message  the actual content of the transaction
	 */
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
