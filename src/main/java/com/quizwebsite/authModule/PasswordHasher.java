package com.quizwebsite.authModule;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    // number of bytes in a salt
    private static final int SALT_LENGTH = 16;
    // number of iterations for PBKDF2
    private static final int ITERATIONS = 65536;
    // length of the key in bits
    private static final int KEY_LENGTH = 256;
    // Generate a random salt
    public String generateSalt() {
        // create a byte array of the specified length
        byte[] salt = new byte[SALT_LENGTH];
        // fill the byte array with random bytes
        new SecureRandom().nextBytes(salt);
        // convert the byte array to a Base64 encoded string
        return Base64.getEncoder().encodeToString(salt);
    }
    // hashes a password using PBKDF2 with HMAC-SHA256
    public String hashPassword(String password, String salt) {
        try {
            // convert the stored Base64 salt back into bytes.
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            // define password hashing parameters
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    saltBytes,
                    ITERATIONS,
                    KEY_LENGTH
            );
            // creates PBKDF2 hash using SHA-256 algorithm
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            // generate a secret key from the password hashing parameters
            byte[] hashBytes = factory.generateSecret(spec).getEncoded();
            // if hashing fails , wrap the exception in a runtime exception
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception exception) {
            throw new RuntimeException("Could not hash password", exception);
        }
    }

    public boolean verifyPassword(String rawPassword, String salt, String expectedHash) {
        String actualHash = hashPassword(rawPassword, salt);
        return actualHash.equals(expectedHash);
    }
}