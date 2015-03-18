package net.waymire.tyranny.common.task;

import java.util.concurrent.TimeUnit;

public interface TaskScheduler
{
	public TaskFuture schedule(Task task, long delay, TimeUnit unit);
	public TaskFuture scheduleAtFixedRate(Task task, long initialDelay, long period, TimeUnit unit);
	public TaskFuture scheduleWithFixedDelay(Task task, long initialDelay, long delay, TimeUnit unit);
	
	public void shutdown();
	public void shutdownNow();
}
