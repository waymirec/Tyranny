package net.waymire.tyranny.common.net;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.protocol.Opcode;
import net.waymire.tyranny.common.protocol.Packet;
import net.waymire.tyranny.common.protocol.ProtocolHandler;

abstract public class BaseTcpServerHandler<T extends Packet<? extends Opcode>> extends BaseIpServerHandler<TcpSession, T> implements TcpServerHandler<T> 
{
	
	public BaseTcpServerHandler(ProtocolHandler<TcpSession,T> protocolHandler)
	{
		super(protocolHandler);
	}
	
	public BaseTcpServerHandler()
	{
		this(null);
	}
	
	@Override
	public void onSessionOpened(TcpSession session) 
	{
		super.onSessionOpened(session);
		session.setAttribute(IpSessionAttributes.SESSION_STATE,TcpSessionState.CONNECTED);
	}

	@Override
	public void onSessionIdle(TcpSession session) 
	{
		session.setAttribute(IpSessionAttributes.SESSION_STATE,TcpSessionState.IDLE);
		super.onSessionIdle(session);
	}

	@Override
	public void onSessionClosed(TcpSession session) 
	{
		session.clearAttributes();
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}	
}
