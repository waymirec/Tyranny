package net.waymire.tyranny.common.task;

import net.waymire.tyranny.common.net.TcpSession;

public abstract class TcpPingTask extends StandardTask 
{
	private TcpSession session;
	
	public TcpPingTask(TcpSession session)
	{
		this.session = session;
	}

	public TcpPingTask()
	{
		
	}
	
	public void setSession(TcpSession session)
	{
		this.session = session;
	}
	
	public TcpSession getSession()
	{
		return session;
	}
	
	@Override
	abstract public void execute();
}
