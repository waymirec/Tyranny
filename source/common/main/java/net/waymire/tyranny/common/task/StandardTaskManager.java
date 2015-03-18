package net.waymire.tyranny.common.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.task.Task.IterationType;

public class StandardTaskManager implements TaskManager
{	
	public static final int DEFAULT_THREAD_COUNT = 200;
	
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final List<TaskFuture> futures = new ArrayList<TaskFuture>();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private Thread manager;
	private int capacity;
	private TaskScheduler scheduler;
	
	public StandardTaskManager()
	{
		this(DEFAULT_THREAD_COUNT);
	}
	
	public StandardTaskManager(int threadCount)
	{
		this.capacity = threadCount;
	}
	
	@Override
	public void setCapacity(int capacity)
	{
		if(running.get())
		{
			throw new IllegalStateException("capacity cannot be set/changed while manager is running.");
		}
		this.capacity = capacity;
	}
	
	@Override
	public int getCapacity()
	{
		return capacity;
	}
	
	@Override
	public void start()
	{
		lock.writeLock().lock();
		try
		{
			if(running.get())
			{
				throw new IllegalStateException("Task Manager is already running");
			}
		
			LogHelper.info(this, "Task Manager ({0}) Starting...", this.getClass().getName());
			running.set(true);
			
			scheduler = initScheduler();
			manager = initManagementThread();
			manager.start();
			
			LogHelper.info(this, "Task Manager ({0}) Started.", this.getClass().getName());
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void stop()
	{
		lock.writeLock().lock();
		try
		{
			if(!running.get())
			{
				throw new IllegalStateException("TaskManager is not running");
			}
			
			LogHelper.info(this, "Task Manager ({0}) Stopping...", this.getClass().getName());
			running.set(false);
			
			List<TaskFuture> work = new ArrayList<TaskFuture>(futures);
			for(TaskFuture future : work)
			{
				cancel(future);
			}
			
			manager.interrupt();
			scheduler.shutdownNow();
			futures.clear();
			
			LogHelper.info(this, "Task Manager ({0}) Stopped.", this.getClass().getName());
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public final boolean isRunning()
	{
		return running.get();
	}
	
	@Override
	public TaskFuture scheduleAtFixedRate(Task task, int delay, int rate, TimeUnit unit)
	{
		lock.writeLock().lock();
		try
		{
			logScheduling(task);
			task.setIterationType(IterationType.FIXED_RATE);
			task.setPeriod(rate);
			task.setTimeUnit(unit);
			task.setup();
			TaskFuture future = scheduler.scheduleAtFixedRate(task, delay, rate, unit);
			futures.add(future);
			return future;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public TaskFuture scheduleWithFixedDelay(Task task, int delay, int rate, TimeUnit unit)
	{
		lock.writeLock().lock();
		try
		{
			logScheduling(task);
			task.setIterationType(IterationType.FIXED_DELAY);
			task.setPeriod(rate);
			task.setTimeUnit(unit);
			task.setup();
			TaskFuture future = scheduler.scheduleWithFixedDelay(task, delay, rate, unit);
			futures.add(future);
			return future;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public TaskFuture schedule(Task task, int delay, TimeUnit unit)
	{
		lock.writeLock().lock();
		try
		{
			logScheduling(task);
			task.setIterationType(IterationType.NONE);
			task.setup();
			TaskFuture future = scheduler.schedule(task, delay, unit);
			futures.add(future);
			return future;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void cancel(final TaskFuture future)
	{
		lock.writeLock().lock();
		try
		{
			if(future != null)
			{
				Task task = future.getTask();
				if(!future.isCancelled())
				{
					logCancelling(task);
					task.disable();
					task.teardown();
					future.cancel(true);
					futures.remove(future);
				}
				else
				{
					LogHelper.info(this, "Failed To Cancel Task [{0}]. Task Already Cancelled?", task == null ? "NULL" : task.getName());
				}
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	protected TaskScheduler initScheduler()
	{
		TaskScheduler scheduler = new StandardTaskScheduler(capacity, new TaskThreadFactory());
		return scheduler;
	}
	
	protected ReentrantReadWriteLock getLock()
	{
		return lock;
	}
	
	protected List<TaskFuture> getTaskFutures()
	{
		return futures;
	}
	
	private Thread initManagementThread()
	{
		Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				while(running.get())
				{
					lock.writeLock().lock();
					try
					{
						for(Iterator<TaskFuture> it = futures.iterator(); it.hasNext(); )
						{
							TaskFuture future = it.next();
							if(future == null || future.isDone())
							{
								it.remove();
							}
						}
					}
					finally
					{
						lock.writeLock().unlock();
					}
					try { Thread.sleep(250); } catch(InterruptedException interruptedException) { }
				}
			}
		};
		return thread;
	}
	
	private void logScheduling(Task task)
	{
		if(LogHelper.isDebugEnabled(this))
		{
			LogHelper.debug(this, "Scheduling Task [{0}].",  task.getName());
			if(!running.get())
			{
				LogHelper.debug(this, "NOTICE: The Task Manager Is Not Running.");
			}
		}
	}
	
	private void logCancelling(Task task)
	{
		if(LogHelper.isDebugEnabled(this))
		{
			LogHelper.debug(this, "Canceling Task [{0}].", task.getName());
			if(!running.get())
			{
				LogHelper.debug(this, "NOTICE: The Task Manager Is Not Running.");
			}
		}
	}	
}
