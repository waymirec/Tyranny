package net.waymire.tyranny.common.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.waymire.tyranny.common.FixedCapacityList;
import net.waymire.tyranny.common.GUID;

public class TaskStatistics
{
	private static TaskStatistics INSTANCE = null;
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final BlockingQueue<StatRecord> queue;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final List<Long> executionTimes = new FixedCapacityList<>(100);
	private final Set<GUID> taskIds = new HashSet<GUID>();
	private final List<StatRecord> longestRunningTasks = new LinkedList<StatRecord>();
	
	private long totalExecutionCount = 0;
	private long minimumExecutionTime = Long.MAX_VALUE;
	private long maximumExecutionTime = Long.MIN_VALUE;
	private long averageExecutionTime = 0;
	
	private Thread thread = null;

	public static TaskStatistics getInstance()
	{
		if(INSTANCE == null)
		{
			synchronized(TaskStatistics.class)
			{
				if(INSTANCE == null)
				{
					INSTANCE = new TaskStatistics();
				}
			}
		}
		return INSTANCE;
	}
	
	private TaskStatistics()
	{
		this.queue = new LinkedBlockingQueue<StatRecord>(200);
	}
	
	public void start()
	{
		if(!running.get())
		{
			lock.writeLock().lock();
			try
			{
				running.set(true);
				thread = new Thread()
				{
					@Override
					public void run()
					{
						while(running.get())
						{
							try
							{
								StatRecord record = null;
								lock.readLock().lock();
								try
								{
									record = queue.poll(500,  TimeUnit.MILLISECONDS);
								}
								finally
								{
									lock.readLock().unlock();
								}
								int cnt = 0;
								while((record != null) && (++cnt < 25))
								{
									lock.writeLock().lock();
									try
									{
										process(record);
									}
									finally
									{
										lock.writeLock().unlock();
									}
									lock.readLock().lock();
									try
									{
										record = queue.poll(10, TimeUnit.MILLISECONDS);
									}
									finally
									{
										lock.readLock().unlock();
									}
								}
								Thread.sleep(500);
							}
							catch(InterruptedException interruptedException)
							{
							}
						}
					}
				};
				thread.start();
			}
			finally
			{
				lock.writeLock().unlock();
			}
		}
	}
	
	public void stop()
	{
		if(running.get())
		{
			lock.writeLock().lock();
			try
			{
				running.set(false);
				thread.interrupt();
			}
			finally
			{
				lock.writeLock().unlock();
			}
		}
	}
	
	public void onTaskExecuteFinished(Task task)
	{
		lock.writeLock().lock();
		task.getLock().readLock().lock();
		try
		{
			StatRecord record = new StatRecord(task);
			queue.offer(record);
		}
		finally
		{
			task.getLock().readLock().unlock();
			lock.writeLock().unlock();
		}
	}

	public Integer getTaskCount()
	{
		lock.readLock().lock();
		try
		{
			return taskIds.size();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	public Long getTotalExecutionCount()
	{
		lock.readLock().lock();
		try
		{
			return totalExecutionCount;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	public Double getMinimumExecutionTime()
	{
		lock.readLock().lock();
		try
		{
			double value = minimumExecutionTime / 1000000000.0;
			BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
			return bd.doubleValue();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	public Double getMaximumExecutionTime()
	{
		lock.readLock().lock();
		try
		{
			double value = maximumExecutionTime / 1000000000.0;
			BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
			return bd.doubleValue();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	public Double getAverageExecutionTime()
	{
		lock.readLock().lock();
		try
		{
			double value = averageExecutionTime / 1000000000.0;
			BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
			return bd.doubleValue();
		}
		finally
		{
			lock.readLock().unlock();	
		}
	}
	
	public List<StatRecord> getLongestRunningTasks()
	{
		return new LinkedList<StatRecord>(longestRunningTasks);
	}
	
	private void process(StatRecord record)
	{
		if(record != null)
		{
			taskIds.add(record.getTaskId());
			executionTimes.add(record.getExecutionTime());
			totalExecutionCount++;

			minimumExecutionTime = minimumExecutionTime < record.getExecutionTime() ? minimumExecutionTime : record.getExecutionTime();
			maximumExecutionTime = maximumExecutionTime > record.getExecutionTime() ? maximumExecutionTime : record.getExecutionTime();
			averageExecutionTime = calculateAverageExecutionTime();
			calculateLongestRunningTasks(record);
		}
	}	

	private void calculateLongestRunningTasks(StatRecord record)
	{
		if(record != null)
		{
			longestRunningTasks.remove(record);
			int index = longestRunningTasks.size();
			for(int i=0; i < longestRunningTasks.size(); i++)
			{
				if(record.getExecutionTime() > longestRunningTasks.get(i).getExecutionTime())
				{
					index = i;
					break;
				}
			}
			longestRunningTasks.add(index,record);
			if(longestRunningTasks.size() > 10)
			{
				longestRunningTasks.retainAll(longestRunningTasks.subList(0, 10));
			}
		}
	}
	
	private long calculateAverageExecutionTime()
	{
		long total = 0;
		for(double time : executionTimes)
		{
			total += time;
		}
		return Math.round(total / executionTimes.size());
	}
	
	public class StatRecord
	{
		private final GUID taskId;
		private final String taskName;
		private final long executionTime;
		
		public StatRecord(Task task)
		{
			this.taskId = task.getId();
			this.taskName = task.getName();
			this.executionTime = (Long)task.getAttribute(TaskAttributes.LAST_EXECUTION_RUN_TIME);
		}

		public GUID getTaskId()
		{
			return taskId;
		}
		
		public String getTaskName()
		{
			return taskName;
		}
		
		public long getExecutionTime()
		{
			return executionTime;
		}
		
		public String getExecutionTimeString()
		{
			double value = executionTime / 1000000000.0;
			BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
			return bd.toPlainString();
		}
		
		@Override
		public boolean equals(Object other)
		{
			if(other instanceof StatRecord)
			{
				return this.getTaskId().equals(((StatRecord)other).getTaskId());
			}
			return false;
		}
	}
}
