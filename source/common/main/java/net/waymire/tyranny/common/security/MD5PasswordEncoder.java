package net.waymire.tyranny.common.security;

import java.security.MessageDigest;
import org.apache.commons.codec.digest.DigestUtils;

/***
 * Provides password encoding and verification using MD5 hashing.
 */
public class MD5PasswordEncoder extends PasswordEncoder {
    public static final String LABEL = null;

    /**
     * Performs the concrete encoding logic. This method uses the
     * <code>DigestUtils.md5Hex()</code> method to perform the encoding.
     * @param password String
     *              Password to be encoded
     * @return byte[]
     *              Encoded password as byte array
     */
    @Override
    protected byte[] doEncode(String password) {
        return DigestUtils.md5Hex(password).getBytes();
    }

    /***
     * Performs the concrete verification logic. This method uses the
     * <code>MessageDigest.isEqual()</code> method to compare the hashes.
     * @param encrypted
     * @param password
     * @return
     */
    @Override
    protected boolean doVerify(byte[] encrypted, String password) {
        byte[] hash = doEncode(password);
        return MessageDigest.isEqual(encrypted,hash);
    }

    /***
     * Returns the label for this encoder
     * @return String
     *          Label for this encoder
     */
    @Override
    protected String getLabel() {
        return LABEL;
    }
}
