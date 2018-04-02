import java.io.*;
import java.security.*;
import java.util.UUID;

public class Utility {
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
        kpg.initialize(1024);
        return kpg.generateKeyPair();
    }

    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }

    private static byte[] marshal(Transaction transaction) throws IOException, IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(transaction);
            return bos.toByteArray();
        }
    }

    private static Object unmarshal(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    public static byte[] generateSignature(PrivateKey privateKey, Transaction transaction) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {
        Signature sign = Signature.getInstance("DSA");
        sign.initSign(privateKey);
        sign.update(marshal(transaction));
        return sign.sign();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        Transaction transaction = new Transaction("212", "asd");
        KeyPair keyPair = generateKeyPair();
        System.out.println(generateSignature(keyPair.getPrivate(), transaction));
    }
}
