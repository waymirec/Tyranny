package net.waymire.tyranny.common.message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.task.StandardTask;
import net.waymire.tyranny.common.task.Task;
import net.waymire.tyranny.common.task.TaskManager;

public class ChainableMessageManager extends BaseMessageManager
{
	private static final int DEFAULT_CAPACITY = 100;
	
	public ChainableMessageManager(int capacity)
	{
		super(capacity * 10);
	}
	
	public ChainableMessageManager()
	{
		this(DEFAULT_CAPACITY);
	}
	
	@Override
	protected void process(final Message message)
	{
		if(this.isTopicSubscribed(message.getTopic()))
		{
			List<MessageListener> list = this.getListeners(message.getTopic());
			for(final MessageListener l : list)
			{
				Task task = new StandardTask()
				{
					@Override
					public void execute()
					{
						if(LogHelper.isTraceEnabled(this))
						{
							LogHelper.trace(this, "Forwarding Message [{0}] To Listener [{1}].", ReflectionToStringBuilder.toString(message), ReflectionToStringBuilder.toString(l));
						}
						
						l.onMessage(message);
						if(message instanceof ChainableMessage)
						{
							if(((ChainableMessage)message).getChainedMessage() !=  null)
							{
								process(((ChainableMessage)message).getChainedMessage());
							}
						}
					}
				};
				String name = this.getClass().getName() + "_" + message.getClass().getName() + "_" + message.getTopic();
				task.setName(name);
				AppRegistry.getInstance().retrieve(TaskManager.class).schedule(task, 10, TimeUnit.MILLISECONDS);
			}
		}
	}	
}