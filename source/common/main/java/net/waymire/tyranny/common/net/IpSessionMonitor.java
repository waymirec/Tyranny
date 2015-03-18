package net.waymire.tyranny.common.net;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.task.Task;
import net.waymire.tyranny.common.task.TaskFuture;
import net.waymire.tyranny.common.task.TaskManager;

abstract public class IpSessionMonitor<S extends IpSession>
{
	private final ReentrantReadWriteLock lock =  new ReentrantReadWriteLock();
	private final Map<S,TaskFuture> sessionMap = new HashMap<>();
	private final Set<S> pendingClose = new HashSet<>();
	private Task masterTask = null;
	private TaskFuture masterFuture = null;
	 
	public IpSessionMonitor()
	{
		this.masterTask = initMasterTask();
	}

	public void start()
	{
		TaskManager taskManager = AppRegistry.getInstance().retrieve(TaskManager.class);
		taskManager.scheduleAtFixedRate(masterTask, 1, 1, TimeUnit.SECONDS);
	}
	
	public void stop()
	{
		TaskManager taskManager = AppRegistry.getInstance().retrieve(TaskManager.class);
		taskManager.cancel(masterFuture);
		for(TaskFuture control : sessionMap.values())
		{
			taskManager.cancel(control);
		}
		sessionMap.clear();
	}
	
	abstract public void add(S session);
	
	protected void add(S session, Task task)
	{
		lock.writeLock().lock();
		try
		{
			TaskManager taskManager = AppRegistry.getInstance().retrieve(TaskManager.class);
			TaskFuture future = taskManager.scheduleAtFixedRate(task, 1, 1, TimeUnit.SECONDS);
			sessionMap.put(session,future);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public void remove(S session)
	{
		lock.writeLock().lock();
		try
		{
			if(sessionMap.containsKey(session))
			{
				TaskManager taskManager = AppRegistry.getInstance().retrieve(TaskManager.class);
				taskManager.cancel(sessionMap.get(session));
				sessionMap.remove(session);
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public boolean isMonitoring(S session)
	{
		lock.readLock().lock();
		try
		{
			return sessionMap.containsKey(session);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	abstract protected Task initMasterTask();
	
	protected ReentrantReadWriteLock getLock()
	{
		return lock;
	}
	
	protected Map<S,TaskFuture> getSessionMap()
	{
		return sessionMap;
	}
	
	protected Set<S> getPendingClose()
	{
		return pendingClose;
	}
	
	protected Task getMasterTask()
	{
		return masterTask;
	}
	
	protected TaskFuture getMasterFuture()
	{
		return masterFuture;
	}
}
