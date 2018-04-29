package utilities;

import java.io.*;
import java.security.*;
import java.util.Base64;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

/**
 * The Utility class provides basic security tools as key pairs and signatures.
 *
 */
public class Utility {
	public final static int DIFFICULITY = 2;
	public final static int BLOCK_SIZE = 1;

	/**
	 * Generates an instance of a private and public key pair using the DSA
	 * algorithm.
	 * 
	 * @return the generated key pair
	 */
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
		kpg.initialize(1024);
		return kpg.generateKeyPair();
	}

	/**
	 * Generates a universally unique identifier (a unique ID).
	 * 
	 * @return the UUID as a string
	 */
	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Signs the transaction using the private key of the sender.
	 * 
	 * @param privateKey
	 *            the private key of the sender
	 * @param transaction
	 *            the transaction to be signed
	 * @return a byte array representing the signed marshalled form of the
	 *         transaction
	 */
	public static String generateSignature(PrivateKey privateKey, String plainText) throws NoSuchAlgorithmException,
			InvalidKeyException, SignatureException, IOException {
		Signature sign = Signature.getInstance("DSA");
		// Configure the private key for the signature
		sign.initSign(privateKey);
		// Sign the marshalled form of the transaction
		sign.update(plainText.getBytes());
		return new String(Base64.getEncoder().encode(sign.sign()));
	}

	public static boolean verfiySignature(PublicKey pubKey, String plainText, String signature) throws Exception {
		Signature sign = Signature.getInstance("DSA");
		sign.initVerify(pubKey);

		sign.update(plainText.getBytes());

		return sign.verify(Base64.getDecoder().decode(signature));
	}

	public static String hash(String s) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(s.getBytes());
		byte byteData[] = md.digest();
		return DatatypeConverter.printHexBinary(byteData);
	}

	/**
	 * Test the key pair generation and the signature methods.
	 */
	// public static void main(String[] args) throws NoSuchAlgorithmException,
	// InvalidKeyException, IOException, SignatureException {
	// Transaction transaction = new Transaction("212", "asd");
	// KeyPair keyPair = generateKeyPair();
	// System.out.println(generateSignature(keyPair.getPrivate(), transaction));
	// }
}
