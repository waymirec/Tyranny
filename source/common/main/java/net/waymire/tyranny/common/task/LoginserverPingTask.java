package net.waymire.tyranny.common.task;

import net.waymire.tyranny.common.net.IpSessionAttributes;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.LoginserverOpcode;
import net.waymire.tyranny.common.protocol.LoginserverPacket;

public class LoginserverPingTask extends TcpPingTask 
{
	public LoginserverPingTask(TcpSession session)
	{
		super(session);
	}

	public LoginserverPingTask()
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

		LoginserverPacket ping = new LoginserverPacket(LoginserverOpcode.PING);
		ping.putLong(nextPingSeq);
		ping.prepare();

		session.setAttribute(IpSessionAttributes.LAST_PING_TX_SEQ,  nextPingSeq);
		session.setAttribute(IpSessionAttributes.LAST_PING_TX_TIME, now);
		session.send(ping);
	}
}
