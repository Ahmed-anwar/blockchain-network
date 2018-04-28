import java.io.*;
import java.security.*;
import java.util.UUID;

/**
 * The Utility class provides basic security tools as key pairs and signatures.
 *
 */
public class Utility {
	/**
	 * Generates an instance of a private and public key pair using the DSA algorithm.
	 * @return the generated key pair
	 */
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
		kpg.initialize(1024);
		return kpg.generateKeyPair();
	}

	/**
	 * Generates a universally unique identifier (a unique ID).
	 * @return the UUID as a string
	 */
	public static String generateUUID(){
		return UUID.randomUUID().toString();
	}

	/**
	 * Marshals a transaction into a reversible form suitable for transmission. 
	 * @param transaction the transaction to be marshalled
	 * @return byte array representing the marshalled form
	 */
	private static byte[] marshal(Transaction transaction) throws IOException, IOException {
		try 	(   ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos); ) {
			out.writeObject(transaction);
			return bos.toByteArray();
		}
	}

	/**
	 * Unmarshals a byte array back into its original object instance.
	 * @param bytes the marshalled form
	 * @return the unmarshalled object
	 */
	@SuppressWarnings("unused")
	private static Object unmarshal(byte[] bytes) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				ObjectInput in = new ObjectInputStream(bis)) {
			return in.readObject();
		}
	}

	/**
	 * Signs the transaction using the private key of the sender.
	 * @param privateKey the private key of the sender
	 * @param transaction the transaction to be signed
	 * @return a byte array representing the signed marshalled form of the transaction
	 */
	public static byte[] generateSignature(PrivateKey privateKey, Transaction transaction) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {
		Signature sign = Signature.getInstance("DSA");
		// Configure the private key for the signature
		sign.initSign(privateKey);
		// Sign the marshalled form of the transaction
		sign.update(marshal(transaction));
		return sign.sign();
	}

	/**
	 * Test the key pair generation and the signature methods.
	 */
	//	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
	//		Transaction transaction = new Transaction("212", "asd");
	//		KeyPair keyPair = generateKeyPair();
	//		System.out.println(generateSignature(keyPair.getPrivate(), transaction));
	//	}
}
