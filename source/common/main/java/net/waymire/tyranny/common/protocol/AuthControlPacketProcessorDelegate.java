package net.waymire.tyranny.common.protocol;

import net.waymire.tyranny.common.net.TcpSession;

public class AuthControlPacketProcessorDelegate extends ProtocolProcessorDelegate<TcpSession, AuthControlPacket> 
{
	
	private static final Class<?>[] argTypes = { TcpSession.class,AuthControlPacket.class };

	public AuthControlPacketProcessorDelegate(Object source,String methodName) {
		super(source,methodName,argTypes);
	}
}