package net.waymire.tyranny.common.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskThreadFactory implements ThreadFactory
{
	private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
	final ThreadGroup group;
	final String namePrefix;
	
	public TaskThreadFactory(final String namePrefix)
	{
		final SecurityManager s = System.getSecurityManager();
        this.group = (s != null)? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
	}
	
	@Override
	public Thread newThread(Runnable runnable)
	{
		String name = namePrefix + THREAD_NUMBER.getAndIncrement();
		if(runnable instanceof Task)
		{
			name = ((Task)runnable).getName();
		}
		
		Thread t = new Thread(group, runnable, name, 0);
		if (!t.isDaemon())
		{
			t.setDaemon(true);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY)
		{
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}

}
