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
	public final static int BLOCK_SIZE = 3;
	public final static int INIT_TTL = 6;
	public final static int BLOCK_LIFE = 3;

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
		sign.initSign(privateKey);
		sign.update(plainText.getBytes());
		return Base64.getEncoder().encodeToString(sign.sign());
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
	
	public static String target() {
		return new String(new char[DIFFICULITY]).replace('\0', '0');
	}
	public static boolean isTarget(String hash) {
		return hash.substring(0, Utility.DIFFICULITY).equals(target());
	}
}
