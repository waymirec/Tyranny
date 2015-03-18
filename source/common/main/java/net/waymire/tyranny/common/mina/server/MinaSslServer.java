package net.waymire.tyranny.common.mina.server;

import java.io.IOException;
import java.net.BindException;

import javax.net.ssl.SSLContext;

import org.apache.mina.filter.ssl.SslFilter;

import net.waymire.tyranny.common.mina.SSLContextGenerator;
import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;

public class MinaSslServer<P extends Packet<? extends Opcode>> extends MinaTcpServer<P> {
	
	public MinaSslServer()
	{
		super();
	}
	
	protected void initSocketAcceptor() throws BindException,IOException
	{
		super.initAcceptor();
		
		String keyStoreFileName = getProperties().getSslKeystore();
		String trustStoreFileName = getProperties().getSslTruststore();
		String storePassword = getProperties().getSslPassword();
		
		SSLContext sslContext = SSLContextGenerator.getSslContext(keyStoreFileName, trustStoreFileName, storePassword);
		getAcceptor().getFilterChain().addFirst("sslFilter",new SslFilter(sslContext));
	}
	
	protected void processMessage(Object message)
	{
		super.processMessage(message);
	}
}
