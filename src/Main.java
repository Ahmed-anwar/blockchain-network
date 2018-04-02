import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class Main {
	/**
	 * Test a network of 200 users and two transaction announcements.
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, CloneNotSupportedException, InvalidKeyException, SignatureException, IOException {
		Network network = new Network();

		for (int i = 0; i < 200; i++)
			network.registerUser();

		network.users.get(0).createTransaction("cyber el roba3y gamed geddan");
		network.users.get(100).createTransaction("han2affel");

		network.print();

	}
}
