package net.waymire.tyranny.authserver.task;

import net.waymire.tyranny.common.net.IpSession;
import net.waymire.tyranny.common.protocol.LoginserverOpcode;
import net.waymire.tyranny.common.protocol.LoginserverPacket;
import net.waymire.tyranny.common.task.StandardTask;

/**
 * Task responsible for sending sending Clock
 * synchronization requests to the remote end
 * of an IP session.
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 *
 */
public class LoginserverClockSyncTask extends StandardTask
{
	private final IpSession session;
	
	public LoginserverClockSyncTask(IpSession session)
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
		
		long now = System.currentTimeMillis();
		LoginserverPacket sync = new LoginserverPacket(LoginserverOpcode.CLOCK_SYNC_REQ);
		sync.putLong(now);
		sync.prepare();
		session.send(sync);
	}
}
