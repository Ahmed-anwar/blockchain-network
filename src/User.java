import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class User {
    private String id;
    private KeyPair myKey;
    public ArrayList<User> peers;
    private Block block;
    private BlockChain blockChain;

    public User(KeyPair keyPair) throws NoSuchAlgorithmException {
        id = UUID.randomUUID().toString();
        myKey = keyPair;
        peers = new ArrayList<>();
        block = new Block();
        blockChain = new BlockChain();

    }

    public void createTransaction(String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, CloneNotSupportedException, IOException {
        Transaction transaction = new Transaction(id, message);
        Notification notification = new Notification(transaction);
        notification.setSignature(Utility.generateSignature(myKey.getPrivate(), transaction));
        notify(notification);
    }

    public void notify(Notification notification) throws CloneNotSupportedException {
        if(block.transactions.contains(notification.transaction))
            return;
        block.add(notification.transaction);
        if(notification.TTL == 0)
            return;

        notification.TTL--;

        int seed = (new Random()).nextInt(peers.size());

        Notification passed = (Notification) notification.clone();
        peers.get(seed).notify(passed);

        int percentageOfNotifiedPeers = new Random().nextInt(15) + 5;
        for(User peer : peers){
            int send = new Random().nextInt(percentageOfNotifiedPeers);
            if(send == 0)
            {
                passed = (Notification) notification.clone();
                peer.notify(passed);
            }

        }
    }

    public void addPeer(User user){
        peers.add(user);
    }

    public void print(){
        System.out.print(id + ": ");
//        System.out.println(peers.toString());
        System.out.println(block);
    }

    public String toString(){
        return id;
    }
}
