package net.waymire.tyranny.common.task;

import java.util.concurrent.atomic.AtomicLong;

public class TrackedTaskWrapper extends TaskWrapper
{
	protected TrackedTaskWrapper(Task task)
	{
		super(task);
	}

	@Override
	protected void beforeRun()
	{
		task.getLock().writeLock().lock();
		try
		{
			task.setAttribute(TaskAttributes.LAST_EXECUTION_START, System.nanoTime());
			task.setAttribute(TaskAttributes.LAST_EXECUTION_STOP, null);
		}
		finally
		{
			task.getLock().writeLock().unlock();
		}
	}
	
	@Override
	protected void afterRun()
	{
		task.getLock().writeLock().lock();
		try
		{
			long now = System.nanoTime();
			long start = (long)task.getAttribute(TaskAttributes.LAST_EXECUTION_START);
			task.setAttribute(TaskAttributes.LAST_EXECUTION_STOP,  now);
			task.setAttribute(TaskAttributes.LAST_EXECUTION_RUN_TIME, now - start);
		
			if(task.hasAttribute(TaskAttributes.EXECUTION_COUNT))
			{
				AtomicLong count = (AtomicLong)task.getAttribute(TaskAttributes.EXECUTION_COUNT);
				count.incrementAndGet();
			}
			else
			{
				task.setAttribute(TaskAttributes.EXECUTION_COUNT, new AtomicLong(1));
			}
		}
		finally
		{
			task.getLock().writeLock().unlock();
		}
		
		TaskStatistics.getInstance().onTaskExecuteFinished(task);
	}
}
