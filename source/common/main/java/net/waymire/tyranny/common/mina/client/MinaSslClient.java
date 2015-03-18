package net.waymire.tyranny.common.mina.client;

import javax.net.ssl.SSLContext;

import org.apache.mina.filter.ssl.SslFilter;

import net.waymire.tyranny.common.mina.SSLContextGenerator;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public class MinaSslClient<P extends Packet<? extends Opcode>> extends MinaTcpClient<P> {
	
	public MinaSslClient()
	{
		super();
	}
	
	@Override
	protected void initializeSocket()
	{
		super.initializeSocket();
		
		String keyStoreFileName = getProperties().getSslKeystore();
		String trustStoreFileName = getProperties().getSslTruststore();
		String storePassword = getProperties().getSslPassword();
		
		SSLContext sslContext = SSLContextGenerator.getSslContext(keyStoreFileName, trustStoreFileName, storePassword);
		SslFilter sslFilter = new SslFilter(sslContext);
		sslFilter.setUseClientMode(true);
		getConnector().getFilterChain().addFirst("sslFilter",sslFilter);
	}
	
	protected void processMessage(Object message)
	{
		super.processMessage(message);
	}
}
