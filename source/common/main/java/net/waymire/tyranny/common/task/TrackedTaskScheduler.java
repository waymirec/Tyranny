package net.waymire.tyranny.common.task;

import java.util.concurrent.ThreadFactory;

public class TrackedTaskScheduler extends StandardTaskScheduler
{
	public TrackedTaskScheduler(int capacity, ThreadFactory factory)
	{
		super(capacity,factory);
	}
	
	@Override
	protected Task decorateTask(final Task task)
	{
		return new TrackedTaskWrapper(task);
	}
}
