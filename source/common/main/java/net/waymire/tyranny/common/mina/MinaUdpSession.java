package net.waymire.tyranny.common.mina;

import org.apache.mina.core.session.IoSession;

import net.waymire.tyranny.common.net.UdpSession;

public class MinaUdpSession extends MinaIpSession implements UdpSession
{
	public MinaUdpSession(IoSession session)
	{
		super(session);
	}
}
