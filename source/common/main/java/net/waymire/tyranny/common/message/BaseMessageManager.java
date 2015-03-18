package net.waymire.tyranny.common.message;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.delegate.Delegate;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.util.ClassUtil;

abstract public class BaseMessageManager implements MessageManager, MessageStatistics
{
	private static final int DEFAULT_CAPACITY = 1000;
	private final Map<String,List<MessageListener>> listeners = new HashMap<String,List<MessageListener>>();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();	
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final AtomicLong publishCount = new AtomicLong(0);
	
	private BlockingQueue<Message> queue;
	private int capacity;
	
	private Thread thread;
	//private TaskFuture taskFuture;
	
	public BaseMessageManager(int capacity)
	{
		this.capacity = capacity; 
	}
	
	public BaseMessageManager()
	{
		this(DEFAULT_CAPACITY);
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
			LogHelper.info(this, "Message Manager ({0}) Starting...", this.getClass().getName());
			queue = new PriorityBlockingQueue<Message>(capacity, new MessagePriorityComparator()); 
			
			Runnable r = initRunnable();
			thread = new Thread(r);
			
			running.set(true);
			thread.start();
			/*
			TaskManager taskManager = AppRegistry.getInstance().retrieve(TaskManager.class);
			Task task = initTask();
			taskFuture = taskManager.scheduleWithFixedDelay(task, 100, 100, TimeUnit.MILLISECONDS);
			running.set(true);
			*/
			LogHelper.info(this, "Message Manager ({0}) Started.", this.getClass().getName());
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
			LogHelper.info(this, "Stopping Message Manager ({0})...", this.getClass().getName());
			running.set(false);
			//AppRegistry.getInstance().retrieve(TaskManager.class).cancel(taskFuture);
			thread.interrupt();
			try
			{
				thread.join(1500);
			}
			catch(InterruptedException interrupted)
			{
				LogHelper.warning(this, "Timeout Waiting for [{0}] Thread To Exit.", this.getClass().getName());
			}
			
			listeners.clear();
			queue.clear();
			LogHelper.info(this, "Message Manager ({0}) Stopped.", this.getClass().getName());
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean isRunning()
	{
		return running.get();
	}
	
	@Override
	public void load(Object target)
	{
		if(target == null)
		{
			throw new IllegalArgumentException();
		}
		
		lock.writeLock().lock();
		try
		{
			Class<?> c = (target instanceof Class<?>) ? (Class<?>)target : target.getClass();
			
			if(LogHelper.isTraceEnabled(this))
			{
				LogHelper.trace(this, "Loading Message Processors For Class [{0}].", c.getName());
			}
			
			List<Method> methods = ClassUtil.getMethodsWithAnnotation(c, MessageProcessor.class);
			for(Method method : methods)
			{
				MessageProcessor annotation = method.getAnnotation(MessageProcessor.class);
				String[] topics = annotation.topic();
				if(topics != null && topics.length > 0)
				{
					for(String topic : annotation.topic())
					{
						if(LogHelper.isTraceEnabled(this))
						{
							LogHelper.trace(this, "Subscribing Processor Method [{0}] For Topic [{1}].",method.getName(), topic);
						}
						MessageListenerDelegate delegate = new MessageListenerDelegate(target, method);
						subscribe(topic, delegate);
					}
				}
				else
				{
					LogHelper.warning(this, "Found Processor Method [{0}] With No Topics Defined.", method.getName());
				}
			}
			
			if(LogHelper.isTraceEnabled(this))
			{
				LogHelper.trace(this, "Done Loading Message Processors For Class [{0}].", c.getName());
			}
			
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void unload(final Object target)
	{
		if(target == null)
		{
			throw new IllegalArgumentException();
		}
		
		lock.writeLock().lock();
		try
		{
			final Class<?> c = (target instanceof Class<?>) ? (Class<?>)target : target.getClass();
			
			if(LogHelper.isTraceEnabled(this))
			{
				LogHelper.trace(this, "Unloading Message Processors For Class [{0}].", c.getName());
			}
			
			for(Entry<String,List<MessageListener>> entry : listeners.entrySet())
			{
				final String topic = entry.getKey();
				final List<MessageListener> tbd = new ArrayList<MessageListener>();
				
				for(final MessageListener listener : entry.getValue())
				{
					final Object t = listener instanceof Delegate ? ((Delegate)listener).getTarget() : listener;
					if(target == t)
					{
						tbd.add(listener);
					}
				}
				
				for(MessageListener listener : tbd)
				{
					if(LogHelper.isTraceEnabled(this))
					{
						if(listener instanceof Delegate)
						{
							LogHelper.trace(this, "Unsubscribing Topic [{0}] From Delegate Method [{1}].",topic, ((Delegate)listener).getMethod().getName());
						}
						else
						{
							LogHelper.trace(this, "Unsubscribing Topic [{0}] From Message Listener [{1}].",topic,listener.getClass().getName());
						}
					}
					unsubscribe(listener);
				}
			}
			
			if(LogHelper.isTraceEnabled(this))
			{
				LogHelper.trace(this, "Done Unloading Message Processors For Class [{0}].", c.getName());
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void subscribe(String topic,MessageListener listener)
	{
		lock.writeLock().lock();
		try
		{
			if(!listeners.containsKey(topic))
			{
				listeners.put(topic, new ArrayList<MessageListener>());
			}
			List<MessageListener> list = listeners.get(topic);
			if(!list.contains(listener))
			{
				if(LogHelper.isDebugEnabled(this))
				{
					LogHelper.debug(this, "Subscribing Listener [{0}] To Topic [{1}].", ReflectionToStringBuilder.toString(listener),topic);
				}
				list.add(listener);
			}
			else
			{
				LogHelper.info(this, "Duplicate Subscription Requested For Listener [{0}] On Topic [{1}].", ReflectionToStringBuilder.toString(listener),topic);
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public void subscribe(Map<String,MessageListener> map)
	{
		lock.writeLock().lock();
		try
		{
			if(map != null)
			{
				for(Entry<String,MessageListener> entry : map.entrySet())
				{
					subscribe(entry.getKey(),entry.getValue());
				}
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void unsubscribe(String topic,MessageListener listener)
	{
		lock.writeLock().lock();
		try
		{
			if(listeners.containsKey(topic))
			{
				if(LogHelper.isDebugEnabled(this))
				{
					LogHelper.debug(this, "Unsubscribing Listener [{0}] From Topic [{1}].", ReflectionToStringBuilder.toString(listener),topic);
				}
				listeners.get(topic).remove(listener);
			}
			else
			{
				LogHelper.info(this,"Unsubscribe Requested For Topic [{0}] For Non-Existent Listener [{1}].",topic,ReflectionToStringBuilder.toString(listener));
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	@Override
	public void unsubscribe(Map<String,MessageListener> map)
	{
		lock.writeLock().lock();
		try
		{
			if(map != null)
			{
				for(Entry<String,MessageListener> entry : map.entrySet())
				{
					unsubscribe(entry.getKey(),entry.getValue());
				}
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void unsubscribe(MessageListener listener)
	{
		lock.writeLock().lock();
		try
		{
			for(String topic : listeners.keySet())
			{
				if(listeners.containsKey(topic))
				{
					unsubscribe(topic,listener);
					//listeners.get(topic).remove(listener);
				}
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public boolean publish(Message message)
	{
		return publish(message,false);
	}
	
	@Override
	public boolean publish(Message message, boolean wait)
	{
		if(LogHelper.isTraceEnabled(this))
		{
			LogHelper.trace(this, "Publishing Message [{0}].", ReflectionToStringBuilder.toString(message));
		}
		
		lock.writeLock().lock();
		try 
		{
			if(wait)
			{
				queue.put(message);
				publishCount.incrementAndGet();
				return true;
			}
			else
			{
				try
				{
					queue.offer(message,500,TimeUnit.MILLISECONDS);
					publishCount.incrementAndGet();
					return true;
				}
				catch(InterruptedException interrupted)
				{
					return false;
				}
			} 
		}
		catch(InterruptedException ie) 
		{ 
			return false; 
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public int getSubscriberCount()
	{
		lock.readLock().lock();
		try
		{
			int count = 0;
			for(List<MessageListener> list : listeners.values())
			{
				count += list.size();
			}
			return count;
		}
		finally
		{
			lock.readLock().unlock();
		} 
	}
	
	@Override
	public int getSubscriberCount(String topic)
	{
		lock.readLock().lock();
		try
		{
			return listeners.containsKey(topic) ? listeners.get(topic).size() : 0;
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public int getPendingCount()
	{
		lock.readLock().lock();
		try
		{
			return queue.size();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	@Override
	public long getPublishedCount()
	{
		return publishCount.get();
	}
	
	abstract protected void process(Message message);
	
	protected ReentrantReadWriteLock getLock()
	{
		return lock;
	}
	
	protected BlockingQueue<Message> getQueue()
	{
		return queue;
	}
	
	protected boolean isTopicSubscribed(String topic)
	{
		lock.readLock().lock();
		try
		{
			return listeners.containsKey(topic);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	protected Set<String> getSubscribedTopics()
	{
		lock.readLock().lock();
		try
		{
			return new HashSet<String>(listeners.keySet());
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	protected List<MessageListener> getListeners(String topic)
	{
		lock.readLock().lock();
		try
		{
			return new ArrayList<MessageListener>(listeners.get(topic));
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	private Runnable initRunnable()
	{
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				while(running.get())
				{
					try
					{
						Message message = queue.poll(500, TimeUnit.MILLISECONDS);
						while(message != null)
						{
							process(message);
							message = queue.poll(10, TimeUnit.MILLISECONDS);
						}
						Thread.sleep(500);
					}
					catch(InterruptedException interrupted)
					{
						
					}
				}
			}
		};
		return r;
	}
	
	/*
	private Task initTask()
	{
		Task task = new StandardTask() {
			public void execute()
			{
				try 
				{
					Message message = queue.poll(500, TimeUnit.MILLISECONDS);
					while(message != null)
					{
						process(message);
						message = queue.poll(10, TimeUnit.MILLISECONDS);
					}
				} 
				catch(InterruptedException ie) 
				{
				}				
			}
		 };
		 task.setName(String.format("MessageManager[%s]",this.getClass().getName()));
		 return task;
	}
	*/
}
