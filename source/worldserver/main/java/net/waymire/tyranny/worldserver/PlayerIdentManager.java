package net.waymire.tyranny.worldserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.annotation.LockField;
import net.waymire.tyranny.common.annotation.Locked;
import net.waymire.tyranny.common.annotation.Locked.LockMode;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.AutoStartable;
import net.waymire.tyranny.common.dynload.Autoload;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.task.StandardTask;
import net.waymire.tyranny.common.task.Task;
import net.waymire.tyranny.common.task.TaskFuture;
import net.waymire.tyranny.common.task.TaskManager;

@Autoload(priority=500)
public class PlayerIdentManager implements AutoInitializable,AutoStartable
{
	private static final long TTL = 15 * 1000;

	@LockField
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private final Map<GUID,PlayerIdentToken> tokens = new HashMap<>();
	
	private TaskFuture future;
	
	@Override
	public void autoInitialize()
	{
		AppRegistry registry = AppRegistry.getInstance();

		registry.retrieve(MessageManager.class).load(this);
		registry.register(PlayerIdentManager.class, this);
	}
		
	@Override
	public void autoDeinitialize()
	{
		AppRegistry.getInstance().unregister(PlayerIdentManager.class);
		AppRegistry.getInstance().retrieve(TaskManager.class).cancel(future);
	}
	
	@Override
	public void autoStart()
	{
		future = AppRegistry.getInstance().retrieve(TaskManager.class).scheduleWithFixedDelay(createTask(), 10, 500, TimeUnit.MILLISECONDS);
	}

	@Override
	public void autoStop() 
	{
		AppRegistry.getInstance().retrieve(TaskManager.class).cancel(future);
	}
	
	@Locked(mode=LockMode.WRITE)
	public void add(GUID tokenId, PlayerIdentToken token)
	{
		tokens.put(tokenId, token);
	}
	
	@Locked(mode=LockMode.WRITE)
	public void remove(GUID tokenId)
	{
		tokens.remove(tokenId);
	}

	@Locked(mode=LockMode.READ)
	public PlayerIdentToken getPlayerIdentToken(GUID tokenId)
	{
		return tokens.get(tokenId);
	}
	
	@Locked(mode=LockMode.READ)
	public List<PlayerIdentToken> getPlayerIdentTokens()
	{
		return new ArrayList<PlayerIdentToken>(tokens.values());
	}
	
	private Task createTask()
	{
		Task task = new StandardTask()
    	{
    		public void execute()
    		{
    			lock.writeLock().lock();
				try
				{
					final Iterator<Map.Entry<GUID,PlayerIdentToken>> it = tokens.entrySet().iterator();
					final long now = System.currentTimeMillis();
					while(it.hasNext())
					{
						final Map.Entry<GUID,PlayerIdentToken> entry = it.next();
						if((entry.getValue().getTimestamp() - now) > TTL)
						{
							if(LogHelper.isDebugEnabled(this))
			        		{
								LogHelper.debug(this, "Expiring IDENT token [%s] for account [%s] at IPv4 address [%s].", entry.getValue().getId(),entry.getKey(), entry.getValue().getInetAddress());
			        		}
							it.remove();
						}
					}
				}
				finally
				{
					lock.writeLock().unlock();
				}
    		}
    	};
    	task.setName(this.getClass().getSimpleName());
    	return task;
	}
}
