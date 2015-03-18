package net.waymire.tyranny.common.message;

import java.util.List;

public class StandardMessageManager extends BaseMessageManager
{
	public StandardMessageManager(int capacity)
	{
		super(capacity);
	}
	
	public StandardMessageManager()
	{
		super();
	}
	
	@Override
	protected void process(Message message)
	{
		getLock().readLock().lock();
		try
		{
			if(this.isTopicSubscribed(message.getTopic()))
			{
				List<MessageListener> list = this.getListeners(message.getTopic());
				for(MessageListener l : list)
				{
					l.onMessage(message);
				}
			}
		}
		finally
		{
			getLock().readLock().unlock();
		}
	}	
}
