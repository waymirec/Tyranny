package net.waymire.tyranny.common.security;

import org.apache.commons.lang.StringUtils;

/*** Provides logic to perform encoding on passwords, as well as verification between an encoded
 * and clear text password.
 */
abstract public class PasswordEncoder {
    public static final String LABEL_DELIMITER = "|";
    public static final String INNER_DELIMITER = ",";
    
    /***
     * Encode a <code>password</code>. The actual logic is held in subclasses.
     * @param password String
     * @return encoded String
     */
    public final String encode(String password)
    {
        byte[] hash = doEncode(password);
        StringBuilder sb = new StringBuilder();
        if(!StringUtils.isEmpty(getLabel()))
        {
        	sb.append(getLabel()).append(INNER_DELIMITER).append(getVersion()).append(LABEL_DELIMITER);
        }
        return sb.append(new String(hash)).toString();
    }

    /***
     * Verify that clear text <code>password</code> matches <code>encrypted</code> password.
     * @param encrypted String
     *          Encrypted form of the password
     * @param password String
     *          Clear text copy of the password
     * @return
     */
    public boolean verify(String encrypted,String password)
    {
        if(!StringUtils.isEmpty(getLabel()) && encrypted.contains(LABEL_DELIMITER))
        {
        	String prefix = encrypted.split("\\"+LABEL_DELIMITER)[0];
        	String[] parts = prefix.split(INNER_DELIMITER);
        	if((parts.length == 2) && (getLabel().equalsIgnoreCase(parts[0])))
            {
                encrypted = encrypted.substring(prefix.length() + LABEL_DELIMITER.length());
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }

        return doVerify(encrypted.getBytes(),password);
    }

    /***
     * Returns the label that is associated with this encoder
     * @return String
     *          Label associated with this encoder.
     */
    protected String getLabel() {
        return null;
    }

    protected int getVersion() {
    	return 1;
    }
    
    /***
     * Subclasses will provide this method to perform the actual encoding logic.
     * @param password String
     *              Password to be encrypted
     * @return byte[]
     *              Encrypted string as a byte array
     */
    abstract protected byte[] doEncode(String password);

    /***
     * Subclasses will provide this method to perform the actual verification logic.
     * @param encrypted byte[]
     *              Password in encrypted form
     * @param password String
     *              Password in clear text form
     * @return boolean
     *              True if passwords match. False otherwise.
     */
    abstract protected boolean doVerify(byte[] encrypted,String password);
}
