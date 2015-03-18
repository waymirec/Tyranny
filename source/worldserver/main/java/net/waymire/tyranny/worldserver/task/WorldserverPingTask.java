package net.waymire.tyranny.worldserver.task;

import net.waymire.tyranny.common.net.IpSession;
import net.waymire.tyranny.common.net.IpSessionAttributes;
import net.waymire.tyranny.common.protocol.LoginserverOpcode;
import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.task.StandardTask;

/**
 * Task responsible for sending Ping packets to a remote
 * end of an IP session.
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 *
 */
public class WorldserverPingTask extends StandardTask
{
	private final IpSession session;
	
	public WorldserverPingTask(IpSession session)
	{
		this.session = session;
	}
	
	@Override
	public void execute()
	{
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
