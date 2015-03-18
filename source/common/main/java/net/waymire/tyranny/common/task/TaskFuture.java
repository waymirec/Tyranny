package net.waymire.tyranny.common.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.waymire.tyranny.common.GUID;

public class TaskFuture
{
	private final GUID guid;
	private final Task task;
	private final RunnableScheduledFuture<?> future;
	
	public TaskFuture(Task task, RunnableScheduledFuture<?> future)
	{
		this.guid = GUID.generate();
		this.task = task;
		this.future = future;
	}
	
	public GUID getId()
	{
		return guid;
	}
	
	public Task getTask()
	{
		return task;
	}
	
	public boolean cancel(boolean interruptIfRunning)
	{
		return future.cancel(interruptIfRunning);
	}
	
	public boolean isCancelled()
	{
		return future.isCancelled();
	}
	
	public boolean isDone()
	{
		return future.isDone();
	}
	
	public Object get() throws InterruptedException,ExecutionException
	{
		return future.get();
	}
	
	public Object get(long timeout, TimeUnit unit) throws InterruptedException,ExecutionException,TimeoutException
	{
		return future.get(timeout,unit);
	}
	
	@Override
	public int hashCode()
	{
		return guid.hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof TaskFuture)
		{
			return guid.equals(((TaskFuture)other).getId());
		}		
		return false;
	}
}
