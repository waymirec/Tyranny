package net.waymire.tyranny.common.net;

import java.util.Iterator;

import net.waymire.tyranny.common.task.StandardTask;
import net.waymire.tyranny.common.task.Task;

abstract public class TcpPingSessionMonitor extends TcpSessionMonitor
{
	private final TcpPingSessionMonitor self;
	
	public TcpPingSessionMonitor()
	{
		this.self = this;

	}
	
	@Override
	protected Task initMasterTask()
	{
		Task task = new StandardTask()
		{
			public void execute()
			{
				getLock().readLock().lock();
				try
				{
					Iterator<TcpSession> sessionIt = getSessionMap().keySet().iterator();
					while(sessionIt.hasNext())
					{
						TcpSession session = sessionIt.next();
						if(session ==  null)
						{
							sessionIt.remove();
							continue;
						}

						long lastPingTxTime = (Long)session.getAttribute(IpSessionAttributes.LAST_PING_TX_TIME);
						long lastPongRxTime = (Long)session.getAttribute(IpSessionAttributes.LAST_PONG_RX_TIME);								
						long delta = Math.round(lastPingTxTime - lastPongRxTime) / 1000;
						long maxIdle = Math.round(session.getIdleTime() * 1.5);
						
						if((maxIdle > 0) && delta >= maxIdle)
						{
							System.out.printf("Closing session due to idle-time: %d / %d\n", delta, maxIdle);
							self.getPendingClose().add(session);									
						}
					}
				}
				finally
				{
					getLock().readLock().unlock();
				}
				
				for(TcpSession session : self.getPendingClose())
				{
					remove(session);
					session.close();
				}
				self.getPendingClose().clear();
			}
		};
		
		return task;
	}
}
