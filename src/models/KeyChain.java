package models;
import java.security.PublicKey;
import java.util.HashMap;


/**
 * Stores and manages the list of public keys of current users of the network.
 *
 */
public class KeyChain {
    private static final HashMap<String, PublicKey> keys = new HashMap<>();

    /**
     * Adds a new public key to the directory mapped to its specified user ID.
     * @param userId the id of the user
     * @param pubKey the public key of the user
     */
    public static void addKey(String userId, PublicKey pubKey){
        keys.put(userId, pubKey);
    }

    public static PublicKey getKey(String userId){
        return keys.get(userId);
    }

    public static void removeKey(String userId){
        keys.remove(userId);
    }
}
