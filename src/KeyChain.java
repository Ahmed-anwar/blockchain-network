import java.security.PublicKey;
import java.util.HashMap;

public class KeyChain {
    private static final HashMap<String, PublicKey> keys = new HashMap<>();

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
