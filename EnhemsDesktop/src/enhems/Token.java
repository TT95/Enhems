package enhems;

	import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Storing token for sessions with server. Uses symmetric key encryption for safety of
 * storing tokens in resources.
 * @author TeoToplak
 *
 */
public class Token {
	
	private static final String pathToToken = "./data/token";
	private static final String pathToUsername = "./data/username";
	/**
	 * this could be made dynamic 
	 */
	private static final String secretKey = "Jor22345BaTe2345";
	private static String algorithm = "AES";
	private static SecretKey key = null;
	private static Cipher c = null;
	
	/**
	 * Initializing instances for enc/dec
	 */
	private static void init() throws NoSuchAlgorithmException, NoSuchPaddingException {
		if(key == null && c == null) {
			key = new SecretKeySpec(secretKey.getBytes(), algorithm);
			c = Cipher.getInstance(algorithm);
		}
	}
	
	private static String decrypt(byte[] crypted) {
		String decrypted = null;
		try {
			init();
			c.init(Cipher.DECRYPT_MODE, key);
			decrypted = new String(c.doFinal(crypted));
		} catch (Exception e) {
		}
		return new String(decrypted);
	}
	
	private static byte[] encrypt(String string) {
		byte[] encrypted = null;
		try {
			init();
			c.init(Cipher.ENCRYPT_MODE, key);
			encrypted = c.doFinal(string.getBytes());
		} catch (Exception e) {
		}
		return encrypted;
	}

	public static String getToken() {
		byte[] bytes = null;
		try {
			Path tokenPath = Paths.get(pathToToken);
			if(!tokenPath.toFile().exists()) {
				tokenPath.toFile().createNewFile();
			}
			bytes = Files.readAllBytes(tokenPath);
		} catch (IOException e) {
			Utilities.showErrorDialog("Error", 
					"Nastala je greška prilikom dohvata tokena! "
					+ "Provjerite da se u direktoriju aplikacije nalazi prikladni folder \"data\".",
					null, e);
			System.exit(1);
		}
		return decrypt(bytes);
	}
	
	public static String getUsername() {
		byte[] bytes = null;
		try {
			Path usernamePath = Paths.get(pathToUsername);
			if(!usernamePath.toFile().exists()) {
				usernamePath.toFile().createNewFile();
			}
			bytes = Files.readAllBytes(usernamePath);
		} catch (IOException e) {
			Utilities.showErrorDialog("Error", 
					"Nastala je greška prilikom dohvata usernamea! "
					+ "Provjerite da se u direktoriju aplikacije nalazi prikladni folder \"data\".",
					null, e);
			System.exit(1);
		}
		return decrypt(bytes);
	}
	

	public static void setToken(String token, String username) {
		
		try {
			Files.write(Paths.get(pathToToken), encrypt(token));
			Files.write(Paths.get(pathToUsername), encrypt(username));
		} catch (IOException e) {
			MyLogger.log("Problem reading username or token file!", e);
		}
	}
	
	public static void removeToken() {
		
		try {
			Files.deleteIfExists(Paths.get(pathToToken));
			Files.deleteIfExists(Paths.get(pathToUsername));
		} catch (IOException e) {
			Utilities.showErrorDialog("Error", "Problem kod dohvacanja log datoteka", null, e);
		}
	}
	
	public static boolean isEmpty() {
		if(getToken().isEmpty()) {
			return true;
		}
		return false;
	}
}
