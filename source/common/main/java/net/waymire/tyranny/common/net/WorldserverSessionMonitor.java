package net.waymire.tyranny.common.net;

import net.waymire.tyranny.common.task.Task;
import net.waymire.tyranny.common.task.WorldserverPingTask;

public class WorldserverSessionMonitor extends TcpPingSessionMonitor
{
	@Override
	public void add(TcpSession session)
	{
		Task task = new WorldserverPingTask(session);
		this.add(session, task);
	}
}
