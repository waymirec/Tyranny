package net.waymire.tyranny.common.protocol;

import net.waymire.tyranny.common.net.TcpSession;

public class LoginserverPacketProcessorDelegate extends ProtocolProcessorDelegate<TcpSession, LoginserverPacket> 
{
	
	private static final Class<?>[] argTypes = { TcpSession.class, LoginserverPacket.class };

	public LoginserverPacketProcessorDelegate(Object source,String methodName) 
	{
		super(source,methodName,argTypes);
	}
}