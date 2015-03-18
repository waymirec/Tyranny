package net.waymire.tyranny.common.net;

import net.waymire.tyranny.common.task.AuthControlPingTask;
import net.waymire.tyranny.common.task.Task;

public class AuthControlSessionMonitor extends TcpPingSessionMonitor 
{
	
	@Override
	public void add(TcpSession session)
	{
		Task task = new AuthControlPingTask(session);
		this.add(session, task);
	}
}
