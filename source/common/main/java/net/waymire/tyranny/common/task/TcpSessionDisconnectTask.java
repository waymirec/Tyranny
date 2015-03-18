package net.waymire.tyranny.common.task;

import net.waymire.tyranny.common.net.TcpSession;

public class TcpSessionDisconnectTask extends StandardTask
{
	private final TcpSession session;
	
	public TcpSessionDisconnectTask(TcpSession session)
	{
		this.session = session;
	}
	
	@Override
	public void execute()
	{
		System.out.printf("Disconnecting Session.\n");
		session.close();
	}
}
