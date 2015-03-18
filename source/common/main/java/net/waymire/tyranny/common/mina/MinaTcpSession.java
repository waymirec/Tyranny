package net.waymire.tyranny.common.mina;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.mina.core.session.IoSession;

import net.waymire.tyranny.common.net.TcpSession;

public class MinaTcpSession extends MinaIpSession implements TcpSession 
{
	
	public MinaTcpSession(IoSession session)
	{
		super(session);
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		return super.equals(other);
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}
