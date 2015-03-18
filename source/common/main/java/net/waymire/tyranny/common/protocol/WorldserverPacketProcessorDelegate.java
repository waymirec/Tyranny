package net.waymire.tyranny.common.protocol;

import net.waymire.tyranny.common.net.TcpSession;

public class WorldserverPacketProcessorDelegate extends ProtocolProcessorDelegate<TcpSession, WorldserverPacket> 
{
	
	private static final Class<?>[] argTypes = { TcpSession.class, LoginserverPacket.class };

	public WorldserverPacketProcessorDelegate(Object source,String methodName) 
	{
		super(source,methodName,argTypes);
	}
}