package net.waymire.tyranny.common.mina;

import java.io.File;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

import net.waymire.tyranny.common.util.ExceptionUtil;

import org.apache.mina.filter.ssl.KeyStoreFactory;
import org.apache.mina.filter.ssl.SslContextFactory;

public final class SSLContextGenerator {
	private static final Logger LOGGER = Logger.getLogger(SSLContextGenerator.class.getName());
	
	private SSLContextGenerator() { }
	
	public static SSLContext getSslContext(String keyStoreFileName, String trustStoreFileName, String storePassword) {
		SSLContext sslContext = null;
		try 
		{
			File keyStoreFile = new File(keyStoreFileName);
			File trustStoreFile = new File(trustStoreFileName);

			if (keyStoreFile.exists() && trustStoreFile.exists()) 
			{
				final KeyStoreFactory keyStoreFactory = new KeyStoreFactory();
				keyStoreFactory.setDataFile(keyStoreFile);
				keyStoreFactory.setPassword(storePassword);

				final KeyStoreFactory trustStoreFactory = new KeyStoreFactory();
				trustStoreFactory.setDataFile(trustStoreFile);
				trustStoreFactory.setPassword(storePassword);

				final SslContextFactory sslContextFactory = new SslContextFactory();
				final KeyStore keyStore = keyStoreFactory.newInstance();
				sslContextFactory.setKeyManagerFactoryKeyStore(keyStore);

				final KeyStore trustStore = trustStoreFactory.newInstance();
				sslContextFactory.setTrustManagerFactoryKeyStore(trustStore);
				sslContextFactory.setKeyManagerFactoryKeyStorePassword(storePassword);
				sslContext = sslContextFactory.newInstance();
			} 
			else 
			{
				LOGGER.log(Level.WARNING,"Keystore or Truststore file does not exist");
			}
		} 
		catch (Exception ex) 
		{
			LOGGER.log(Level.SEVERE,ExceptionUtil.getReason(ex));
			LOGGER.log(Level.SEVERE,ExceptionUtil.getStackTrace(ex));
		}
		return sslContext;
	}
}
