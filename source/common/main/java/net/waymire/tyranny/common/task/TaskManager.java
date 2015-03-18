package net.waymire.tyranny.common.task;

import java.util.concurrent.TimeUnit;

public interface TaskManager
{
	public TaskFuture scheduleAtFixedRate(Task task, int delay, int rate, TimeUnit unit);
	public TaskFuture scheduleWithFixedDelay(Task task, int delay, int rate, TimeUnit unit);
	public TaskFuture schedule(Task task, int delay, TimeUnit unit);
	public boolean isRunning();
	public void cancel(final TaskFuture future);
	public void start();
	public void stop();
	public void setCapacity(int capacity);
	public int getCapacity();
}
