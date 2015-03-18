package net.waymire.tyranny.common.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.task.Task.IterationType;

public class TrackedTaskManager extends StandardTaskManager
{
	public static final int DEFAULT_MAX_EXECUTION_TIME = 5000;
	
	private int maxExecutionTime;
	private Thread monitor = null;
	
	public TrackedTaskManager(int capacity, int maxExecutionTime)
	{
		super(capacity);
		this.maxExecutionTime = maxExecutionTime;
	}
	
	public TrackedTaskManager(int maxExecutionTime)
	{
		super();
		this.maxExecutionTime = maxExecutionTime;
	}
	
	public TrackedTaskManager()
	{
		this(DEFAULT_MAX_EXECUTION_TIME);
	}
	
	public void setMaxExecutionTime(int maxExecutionTime)
	{
		this.maxExecutionTime = maxExecutionTime;
	}
	
	public int getMaxExecutionTime()
	{
		return maxExecutionTime;
	}
	
	@Override
	public void start()
	{
		super.start();
		
		this.getLock().writeLock().lock();		
		try
		{
			monitor = initMonitorThread();
			monitor.start();
		}
		finally
		{
			this.getLock().writeLock().unlock();
		}
	}
	
	@Override
	public void stop()
	{
		super.stop();
		
		this.getLock().writeLock().lock();		
		try
		{
			monitor.interrupt();
		}
		finally
		{
			this.getLock().writeLock().unlock();
		}
	}
	
	@Override
	protected TaskScheduler initScheduler()
	{
		TaskScheduler scheduler = new TrackedTaskScheduler(this.getCapacity(), new TaskThreadFactory());
		return scheduler;
	}
	
	private Thread initMonitorThread()
	{
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				while(isRunning())
				{
					try
					{
						getLock().readLock().lock();
						List<TaskFuture> futures = new ArrayList<>(getTaskFutures());
						getLock().readLock().unlock();

						for(TaskFuture future : futures)
						{
							if(future != null && !future.isDone() && !future.isCancelled())
							{
								Task task = future.getTask();
								if(task != null)
								{
									task.getLock().readLock().lock();
									try
									{
										Long start = (Long)task.getAttribute(TaskAttributes.LAST_EXECUTION_START);
										Long stop = (Long)task.getAttribute(TaskAttributes.LAST_EXECUTION_STOP);
										long now = System.nanoTime();
							
										if(start != null)
										{
											if(stop == null)
											{
												long delta = TimeUnit.MILLISECONDS.convert((now - start), TimeUnit.NANOSECONDS);
												if(delta >= maxExecutionTime)
												{
													LogHelper.warning(this, "Task [{0}] Has Exceeded The Maximum Run Time Of [{1}ms] And Is Being Cancelled.", task.getName(), maxExecutionTime);
													future.cancel(true);
												}
											}
											else if (IterationType.FIXED_RATE == task.getIterationType() && task.getTimeUnit() != null)
											{
												long delta = TimeUnit.MILLISECONDS.convert((now - start), TimeUnit.NANOSECONDS);
												long period = TimeUnit.MILLISECONDS.convert(task.getPeriod(), task.getTimeUnit());
												long max = Math.round(period * 1.25);
												if(delta >= max)
												{
													if(LogHelper.isDebugEnabled(this))
													{
														LogHelper.debug(this, "Task [{0}] With A [{1}] Fixed Rate Of [{2} ms] Has Not Executed For [{3} ms].", task.getName(), task.getIterationType().toString(), period, delta);
													}
												}
											}
											else if(IterationType.FIXED_DELAY == task.getIterationType() && task.getTimeUnit() != null)
											{
												long delta = TimeUnit.MILLISECONDS.convert((now - stop), TimeUnit.NANOSECONDS);
												long period = TimeUnit.MILLISECONDS.convert(task.getPeriod(), task.getTimeUnit());
												long max = Math.round(period * 1.25);
												if(delta >= max)
												{
													if(LogHelper.isDebugEnabled(this))
													{
														LogHelper.info(this, "Task [{0}] With A [{1}] Period Of [{2} ms] Has Not Executed For [{3} ms].", task.getName(), task.getIterationType().toString(), period, delta);
													}
												}
											}
										}
									}
									finally
									{
										task.getLock().readLock().unlock();
									}
								}
								Thread.sleep(500);
							}
						}
					}
					catch(InterruptedException interruptedException)
					{
							
					}
				}
			}
		};
		return new Thread(r);
	}
}
