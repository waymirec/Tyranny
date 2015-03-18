package net.waymire.tyranny.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.delegate.Delegate;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.task.StandardTask;
import net.waymire.tyranny.common.task.Task;
import net.waymire.tyranny.common.task.TaskFuture;
import net.waymire.tyranny.common.task.TaskManager;

/**
 * ThreadedQueue is a simplified publish-subscribe class that allows other
 * threads/sources to publish items of type <T> to this object where it will be
 * queued and consumed.
 *
 * @param <T>		The type of object the queue will process.
 * 
 * @author Chris Waymire <cwaymire@apriva.com>
 */
public class AsyncQueue<T>
{
    public static final int DEFAULT_CAPACITY = 1000;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final BlockingQueue<T> queue;
    private final Delegate delegate;
    private Task task;
    private TaskFuture future;
    
    public AsyncQueue(Delegate target, int capacity)
    {
        this.delegate = target;
        this.queue = new LinkedBlockingQueue<T>(capacity);
    }

    public AsyncQueue(Delegate delegate)
    {
        this(delegate, DEFAULT_CAPACITY);
    }

    /**
     * Start the AsyncQueue
     */
    public void start()
    {
        lock.writeLock().lock();
        try
        {
        	if(future == null || future.isCancelled() || future.isDone())
        	{
        		TaskManager taskManager = AppRegistry.getInstance().retrieve(TaskManager.class);
        		initTask();
        		if(LogHelper.isDebugEnabled(this))
        		{
        			LogHelper.debug(this, "Starting Task: [{0}].", task.getName());
        		}
        		future = taskManager.scheduleWithFixedDelay(task, 100, 100, TimeUnit.MILLISECONDS);
        	}
            else
            {
            	LogHelper.info(this,"Failed to Start Thread [{0}]: thread already running.", task.getName());
            }
        } finally
        {
            lock.writeLock().unlock();
        }
    }

    /**
     * Stop the AsyncQueue
     */
    public void stop()
    {
        lock.writeLock().lock();
        try
        {
        	if(future != null && !future.isDone() && !future.isCancelled())
        	{
        		if(LogHelper.isDebugEnabled(this))
        		{
        			LogHelper.debug(this, "Stopping Task: [{0}].", task.getName());
        		}
        		AppRegistry.getInstance().retrieve(TaskManager.class).cancel(future);
            }
            else
            {
            	LogHelper.info(this, "Failed to Stop Task [{0}]: task not running.", task == null ? "NULL" : task.getName());
            }
        	queue.clear();
        } finally
        {
            lock.writeLock().unlock();
        }
    }

    /**
     * Determine whether the AsyncQueue is actively running.
     * 
     * @return			<code>true</code> if the AsyncQueue is running.
     * 					<code>false</code> otherwise.
     */
    public boolean isRunning()
    {
    	return (future != null && !future.isDone() && !future.isCancelled());
    }
    
    /**
     * Add <code>item</code> to the AsyncQueue queue for processing.
     * 
     * @param item			Item to add to the queue.
     */
    public void put(T item)
    {
        lock.writeLock().lock();
        try
        {
            queue.put(item);
        } catch (InterruptedException ie)
        {
            put(item);
        } finally
        {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
    
    /**
     * Initialize the AsyncQueue polling thread.
     */
    private void initTask()
    {
    	task = new StandardTask()
    	{
    		public void execute()
    		{
    			try
    			{
    				T item = queue.poll(500,  TimeUnit.MILLISECONDS);
    				int cnt = 0;
    				while(item != null && (++cnt < 25))
    				{
    					delegate.invoke(item);
    					item = queue.poll(10, TimeUnit.MILLISECONDS);
    				}
    			}
    			catch(InterruptedException interruptedException)
    			{
    			}
    		}
    	};
    	task.setName(this.getClass().getSimpleName());
    }    
}