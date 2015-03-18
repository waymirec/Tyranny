package net.waymire.tyranny.common.security;

/***
 * Factory for creating PasswordEncoder objects.
 */
public final class PasswordEncoderFactory {
	private PasswordEncoderFactory() { }
	
    /***
     * Creates the necessary PasswordEncoder to handle <code>encodedPassword</code>
     * @param encodedPassword String
     *              A password in encoded form as a String
     * @return PasswordEncoder
     */
    public static PasswordEncoder createPasswordEncoder(String encodedPassword) {
        // if the encoded password has a label, we expect to find the delimiter
        // within the first 16 characters. This makes the search a little more
        // efficient
        if(encodedPassword.substring(0,16).contains("|")) {
            // we assume we have a label so we split the encoded string on the "|"
            String prefix = encodedPassword.split("\\|")[0];
            // we then split the prefix into its label and version components
            String[] parts = prefix.split("\\,");
            // and we take the first part as the label
            String label = parts[0];
            // and the second part as our version
            Integer version = Integer.parseInt(parts[1]);

            // in the current version we only support the SSHA label
            if(label.equalsIgnoreCase("SSHA")) {
            	String packageName = "net.waymire.tyranny.common.security";
            	String className = String.format("%s.SSHAPasswordEncoderV%d", packageName,version);
            	try
            	{
            		@SuppressWarnings("unchecked")
					Class<? extends PasswordEncoder> c = (Class<? extends PasswordEncoder>)Class.forName(className);
            		return c.newInstance();
            	}
            	catch(Exception e)
            	{
            		throw new RuntimeException(e);
            	}
            }
            throw new IllegalArgumentException(label);
        }

        // if there was no label delimiter we assume that this is a
        // legacy MD5 encoded password
        return new MD5PasswordEncoder();
    }
}
