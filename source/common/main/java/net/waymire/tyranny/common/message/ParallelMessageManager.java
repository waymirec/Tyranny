package net.waymire.tyranny.common.message;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelMessageManager extends BaseMessageManager
{
	private final ExecutorService executor = Executors.newCachedThreadPool();
	
	public ParallelMessageManager(int capacity)
	{
		super(capacity);
	}
	
	public ParallelMessageManager()
	{
		super();
	}
	
	@Override
	protected void process(final Message message)
	{
		getLock().readLock().lock();
		try
		{
			if(this.isTopicSubscribed(message.getTopic()))
			{
				List<MessageListener> list = getListeners(message.getTopic());
				for(final MessageListener l : list)
				{
					executor.execute(
							new Runnable()
							{
								public void run()
								{
									l.onMessage(message);
								}
							}
					);
				}
			}
		}
		finally
		{
			getLock().readLock().unlock();
		}
	}	
}