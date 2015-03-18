package net.waymire.tyranny.common.security;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;

/***
 * Provides password encoding and verification using SSHA hashing.
 */
public class SSHAPasswordEncoderV1 extends PasswordEncoder {
    public static final String LABEL = "SSHA";
    private static final int VERSION = 1;
    private static final String CHARSET_NAME = "UTF-8";
    private static final String RNG_ALGORITHM = "SHA1PRNG";
    private static final String DIGEST_ALGORITHM = "SHA-256";
    private static final int DIGEST_LENGTH = 32;
    private static final int SALT_LENGTH = 4;
    private static final int ITERATIONS = 5;

    /**
     * Performs the concrete encoding logic.
     * This method uses a custom algorithm to encode strings
     * utilizing the SHA1PRNG/SHA-256 algorithm.
     * @param password String
     *              Password to be encoded
     * @return byte[]
     *              Encoded password as byte array
     */
    @Override
    protected byte[] doEncode(String password) {
        try {
            /* generate salt randomly */
            byte[] salt = new byte[SALT_LENGTH];
            SecureRandom.getInstance(RNG_ALGORITHM).nextBytes(salt);

            final MessageDigest sha = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] pw = password.getBytes(CHARSET_NAME);

            // we make any attempts to break the encoding more difficult
            // by hashing the value multiple times. The more iterations
            // the more difficult it will be for someone to break it.
            for (int i = 0; i < ITERATIONS; i++) {
                sha.reset();
                sha.update(pw);
                sha.update(salt);
                pw = sha.digest();
            }

            // once we have our hashed value we concatenate
            // the salt that was used onto the end of it
            // and return it BASE64 encoded
            byte[] temp = new byte[pw.length + salt.length];
            System.arraycopy(pw, 0, temp, 0, pw.length);
            System.arraycopy(salt, 0, temp, pw.length, salt.length);
            return Base64.encodeBase64(temp);
        } catch (GeneralSecurityException gse) {
            throw new PasswordSecurityException(gse);
        } catch (UnsupportedEncodingException uee) {
            throw new PasswordSecurityException(uee);
        }
    }

    /***
     * Performs the concrete verification logic for SSHA encoding.
     * @param encrypted
     * @param password
     * @return
     */
    @Override
    protected boolean doVerify(byte[] encrypted, String password) {
        try {
            final MessageDigest sha = MessageDigest.getInstance(DIGEST_ALGORITHM);

            // BASE64 decode the encoded value
            encrypted = Base64.decodeBase64(encrypted);

            // once we have it decoded we can split the bytes
            // into their distinct parts; the hashed value and
            // the salt that was concatenated to it. The digest
            // is a fixed length so we always know where the salt
            // starts.
            byte[] hash = new byte[DIGEST_LENGTH];
            byte[] salt = new byte[encrypted.length - DIGEST_LENGTH];
            System.arraycopy(encrypted, 0, hash, 0, DIGEST_LENGTH);
            System.arraycopy(encrypted, DIGEST_LENGTH, salt, 0, salt.length);

            // now that we have the stored/known hash value we want to
            // hash the provided password to see if it matches.
            byte[] pwhash = password.getBytes(CHARSET_NAME);

            // its important that the same number of hash
            // iterations be used here as was used to hash
            // the stored/known value or they will never match
            for (int i = 0; i < ITERATIONS; i++) {
                sha.reset();
                sha.update(pwhash);
                sha.update(salt);
                pwhash = sha.digest();
            }

            // are the two values the same?
            return MessageDigest.isEqual(hash, pwhash);
        } catch (GeneralSecurityException gse) {
            throw new PasswordSecurityException(gse);
        } catch (UnsupportedEncodingException uee) {
            throw new PasswordSecurityException(uee);
        }
    }

    /***
     * Returns the label for this encoder
     * @return String
     *          Label for this encoder
     */
    @Override
    protected String getLabel()
    {
        return LABEL;
    }
    
    /***
     * Returns the version of this encoder
     * 
     * @return int Version of the encoder
     */
    protected int getVersion()
    {
    	return VERSION;
    }
}
