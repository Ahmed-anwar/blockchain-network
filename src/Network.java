import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Random;

public class Network {
    static final ArrayList<User> users = new ArrayList<>();

    public void registerUser() throws NoSuchAlgorithmException {
        User newUser = new User(Utility.generateKeyPair());

        if(users.isEmpty()){
            users.add(newUser);
            return;
        }

        int seed = (new Random()).nextInt(users.size());
        User seedUser = users.get(seed);
        newUser.addPeer(seedUser);
        seedUser.addPeer(newUser);

        int percentageOfEdges = new Random().nextInt(15) + 5;

        for (int i = 0; i < users.size(); i++) {
            int edge = new Random().nextInt(percentageOfEdges);
            if(edge == 0 && i != seed){
                User peer = users.get(i);
                newUser.addPeer(peer);
                peer.addPeer(newUser);
            }
        }
        users.add(newUser);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, CloneNotSupportedException, InvalidKeyException, SignatureException, IOException {
        Network network = new Network();
        for (int i = 0; i < 200; i++)
            network.registerUser();

        User user = users.get(0);
        user.createTransaction("Hagar");
//        network.print();

        int counter = 1;
        for (User u : users){
            System.out.println("=========================================================================");
            System.out.println("=============================== User #" + counter++ + " ===============================");
            System.out.println("=========================================================================");
            System.out.println("User ID: " + u);
            System.out.println("Peers: " + u.peers.toString());
            System.out.println(" ");
        }
    }

    public void print(){
        for(User user: users)
            user.print();
    }


}
