package net.waymire.tyranny.common.net;

import net.waymire.tyranny.common.task.LoginserverPingTask;
import net.waymire.tyranny.common.task.Task;

public class LoginserverSessionMonitor extends TcpPingSessionMonitor 
{

	@Override
	public void add(TcpSession session)
	{
		Task task = new LoginserverPingTask(session);
		this.add(session, task);
	}
}
