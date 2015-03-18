package net.waymire.tyranny.common.task;

import java.util.concurrent.Executors;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class StandardTaskScheduler implements TaskScheduler
{
	private ScheduledThreadPoolExecutor executor;
	
	public StandardTaskScheduler(int capacity, ThreadFactory factory)
	{
		ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(capacity, new TaskThreadFactory());
		executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		executor.setRemoveOnCancelPolicy(true);

		this.executor = executor;		
	}
	
	@Override
	public TaskFuture schedule(Task task, long delay, TimeUnit unit)
	{
		Task decorated = decorateTask(task);
		RunnableScheduledFuture<?> future = (RunnableScheduledFuture<?>)executor.schedule(decorated, delay, unit);
		return wrapFuture(task, future);
	}

	@Override
	public TaskFuture scheduleAtFixedRate(Task task, long initialDelay, long period, TimeUnit unit)
	{
		Task decorated = decorateTask(task);
		RunnableScheduledFuture<?> future = (RunnableScheduledFuture<?>)executor.scheduleAtFixedRate(decorated, initialDelay, period, unit);
		return wrapFuture(task,future);
	}

	@Override
	public TaskFuture scheduleWithFixedDelay(Task task, long initialDelay, long delay, TimeUnit unit)
	{
		Task decorated = decorateTask(task);
		RunnableScheduledFuture<?> future = (RunnableScheduledFuture<?>)executor.scheduleWithFixedDelay(decorated, initialDelay, delay, unit);
		return wrapFuture(task,future);
	}

	@Override
	public void shutdown()
	{
		executor.shutdown();
	}
	
	@Override
	public void shutdownNow()
	{
		executor.shutdownNow();
	}
	
	protected Task decorateTask(Task task)
	{
		return task;
	}
	
	private TaskFuture wrapFuture(Task task, RunnableScheduledFuture<?> future)
	{
		TaskFuture taskFuture = new TaskFuture(task,future);
		task.setFuture(taskFuture);
		return taskFuture;
	}
}
