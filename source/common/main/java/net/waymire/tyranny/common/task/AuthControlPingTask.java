package net.waymire.tyranny.common.task;

import net.waymire.tyranny.common.net.IpSessionAttributes;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.AuthControlOpcode;
import net.waymire.tyranny.common.protocol.AuthControlPacket;

public class AuthControlPingTask extends TcpPingTask 
{
	public AuthControlPingTask(TcpSession session)
	{
		super(session);
	}

	public AuthControlPingTask()
	{
		
	}
	
	@Override
	public void execute()
	{
		TcpSession session = this.getSession();
		if(session == null || !session.isValid() || !session.isConnected())
		{
			this.disable();
			return;
		}
		
		long lastPingSeq = (Long)session.getAttribute(IpSessionAttributes.LAST_PING_TX_SEQ);
		long nextPingSeq = ++lastPingSeq;
		long now = System.currentTimeMillis();
		
		AuthControlPacket ping = new AuthControlPacket(AuthControlOpcode.PING);
		ping.putLong(nextPingSeq);
		ping.prepare();
		
		session.setAttribute(IpSessionAttributes.LAST_PING_TX_SEQ,  nextPingSeq);
		session.setAttribute(IpSessionAttributes.LAST_PING_TX_TIME, now);
		session.send(ping);
	}
}
