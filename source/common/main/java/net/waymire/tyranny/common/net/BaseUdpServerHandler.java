package net.waymire.tyranny.common.net;

import java.util.logging.Logger;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.protocol.ProtocolHandler;

public abstract class BaseUdpServerHandler<T extends Packet<? extends Opcode>> extends BaseIpServerHandler<UdpSession, T> implements UdpServerHandler<T>
{
	private static final String CLASSNAME = BaseUdpServerHandler.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASSNAME);
	
	public BaseUdpServerHandler(ProtocolHandler<UdpSession,T> protocolHandler)
	{
		super(protocolHandler);
	}
	
	public BaseUdpServerHandler()
	{
		super();
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	public Logger getLogger()
	{
		return LOGGER;
	}
}
