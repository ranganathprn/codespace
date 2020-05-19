package codespace;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/**
 * This class is used to generate the encrypted password using PBE, with the help of random salt value.
 * The final password is encoded using Base64
 * @author ranga
 *
 */
 
public class PasswordUtil {
    
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    //number of times the password is hashed
    private static final int ITERATIONS = 100;
    
    //key is dervied in 256 bits long
    private static final int DERIVE_KEY_LENGTH = 256;
    
    //salt key length.
    private static final int SALT_LEN = 32;
    
    /**
     * This method generates 32 bit random value, that will help generating the
     * secured passed, during the hashing process.
     * @return
     */
    
     public static String getSalt() {
        StringBuilder returnValue = new StringBuilder(SALT_LEN);

        for (int i = 0; i < SALT_LEN; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

    private static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, DERIVE_KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }
    /**
     * This function generates the secured password, we need to call the getSalt() and pass that
     * value to this function.
     * @param password
     * @param salt
     * @return Base64 encoded value is returned.
     */
    public static String generateSecurePassword(String password, String salt) {
        String returnValue = null;

        byte[] securePassword = hash(password.toCharArray(), salt.getBytes());
 
        returnValue = Base64.getEncoder().encodeToString(securePassword);
 
        return returnValue;
    }
    
    /**
     *  This function return true when the providedPassword and securedPassword from DB is same.
     *  We need to pass the same salt value, which is stored in the db, during generating the secured
     *  Password.
     * @param providedPassword
     * @param securedPassword
     * @param salt
     * @return boolean
     */
    public static boolean verifyUserPassword(String providedPassword,
            String securedPassword, String salt)
    {
        boolean returnValue = false;
        
        // Generate New secure password with the same salt
        String newSecurePassword = generateSecurePassword(providedPassword, salt);
        
        // Check if two passwords are equal
        returnValue = newSecurePassword.equalsIgnoreCase(securedPassword);
        
        return returnValue;
    }
    
    
    public static void main(String []args) {
    	
    	final String userPassword = new String("Password@123!");
    	String salt = PasswordUtil.getSalt();
    	String securedPassword = PasswordUtil.generateSecurePassword(userPassword, salt);
    	
    	System.out.println("Secure password: "+ securedPassword);
    	
    	System.out.println("Salt value: "+ salt);
    	if(PasswordUtil.verifyUserPassword(userPassword, securedPassword, salt)){
    		System.out.println("Valid password");
    	}
    	
    	
    	
    	
    }
}
