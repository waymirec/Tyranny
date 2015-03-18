package net.waymire.tyranny.common.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskThreadFactory implements ThreadFactory
{
	static final AtomicInteger poolNumber = new AtomicInteger(1);
	final ThreadGroup group;
	final AtomicInteger threadNumber = new AtomicInteger(1);
	final String namePrefix;
	
	public TaskThreadFactory()
	{
		SecurityManager s = System.getSecurityManager();
        group = (s != null)? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" + poolNumber.getAndIncrement() + "-task-";
	}
	
	@Override
	public Thread newThread(Runnable runnable)
	{
		String name = namePrefix + threadNumber.getAndIncrement();
		if(runnable instanceof Task)
		{
			name = ((Task)runnable).getName();
		}
		
		Thread t = new Thread(group, runnable, name, 0);
		if (t.isDaemon())
		{
			t.setDaemon(false);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY)
		{
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}

}
