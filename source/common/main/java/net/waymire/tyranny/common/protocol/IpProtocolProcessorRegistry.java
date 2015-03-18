package net.waymire.tyranny.common.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.net.IpSession;
import net.waymire.tyranny.common.task.StandardTask;
import net.waymire.tyranny.common.task.Task;
import net.waymire.tyranny.common.task.TaskFuture;
import net.waymire.tyranny.common.task.TaskManager;

public class IpProtocolProcessorRegistry<S extends IpSession, P extends Packet<? extends Opcode>> implements ProtocolHandler<S,P>,ProtocolProcessorRegistry<S,P> 
{
	public static final int DEFAULT_CAPACITY = 1000;
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<Opcode,List<ProtocolProcessor<S,P>>> handlers = new HashMap<Opcode,List<ProtocolProcessor<S,P>>>();
	private final BlockingQueue<QueueItem> messages;

	private TaskFuture taskFuture;
	
	public IpProtocolProcessorRegistry()
	{
		this(DEFAULT_CAPACITY);
	}
	
	public IpProtocolProcessorRegistry(int capacity)
	{
		this.messages = new LinkedBlockingQueue<QueueItem>(capacity);
	}
	
	@Override
	public void start()
	{
		lock.writeLock().lock();
		try
		{
			if(taskFuture == null || (!taskFuture.isCancelled() && !taskFuture.isDone()))
			{
				TaskManager taskManager = AppRegistry.getInstance().retrieve(TaskManager.class);
				messages.clear();
				
				Task task = new StandardTask(){ 
					public void execute() 
					{  
						try 
						{ 
							QueueItem item = messages.poll(500, TimeUnit.MILLISECONDS);
							while(item != null)
							{
								processQueueItem(item);
								item = messages.poll(10, TimeUnit.MILLISECONDS);
							}
						} 
						catch(InterruptedException ie) 
						{
						} 
				} };
				task.setName(String.format("ProtocolProcessorRegistry[%s]",this.getClass().getName()));
				taskFuture = taskManager.scheduleWithFixedDelay(task, 100, 100, TimeUnit.MILLISECONDS);
			}
			else
			{
				LogHelper.info(this, "Failed to Start. Task Is Already Running.", taskFuture.getId());
			}
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
			if(taskFuture != null && (taskFuture.isCancelled() || taskFuture.isDone()))
			{
				AppRegistry.getInstance().retrieve(TaskManager.class).cancel(taskFuture);
			}
			else
			{
				LogHelper.info(this, "Unable To Process Stop Request. Task Is Not Running.");
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void register(Opcode opcode,ProtocolProcessor<S,P> processor)
	{
		lock.writeLock().lock();
		try
		{
			if(!handlers.containsKey(opcode))
			{
				handlers.put(opcode, new ArrayList<ProtocolProcessor<S,P>>());
			}
			if(!handlers.get(opcode).contains(processor))
			{
				handlers.get(opcode).add(processor);
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void deregister(Opcode opcode)
	{
		lock.writeLock().lock();
		try
		{
			if(handlers.containsKey(opcode))
			{
				handlers.remove(opcode);
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void deregister(Opcode opcode,ProtocolProcessor<S,P> processor)
	{
		lock.writeLock().lock();
		try
		{
			if(handlers.containsKey(opcode))
			{
				handlers.get(opcode).remove(processor);
			}
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void handle(S session,P packet)
	{
		lock.writeLock().lock();
		try
		{
			QueueItem item = new QueueItem(session,packet);
			messages.put(item);
		}
		catch (InterruptedException ie)
		{
			handle(session,packet);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	@SuppressWarnings("unchecked")
	private void processQueueItem(final QueueItem item)
	{
		lock.readLock().lock();
		try
		{
			S session = item.getSession();
			P packet = (P)item.getPacket();
			Opcode opcode = packet.opcode();
			
			List<ProtocolProcessor<S,P>> list = handlers.get(opcode);
			if ((list != null) && !list.isEmpty()) {
				for (ProtocolProcessor<S,P> handler : list) {
					if (handler == null) 
					{
						LogHelper.info(this,"Protocol handler is null. Deregistering...");
						deregister(opcode);
						return;
					}
					if(LogHelper.isTraceEnabled(this))
					{
						LogHelper.trace(this, "Forwarding Packet With Opcode [{0}] To Handler [{1}].", packet.opcode(), handler.toString());
					}
					handler.process(session, packet);
				}
			}
			else
			{
				LogHelper.warning(this, "No Handler Found For Packet Opcode [{0}].", packet.opcode());
			}
		}
		finally
		{
			lock.readLock().unlock();
		}
		
	}
	
	private class QueueItem {
		private final S session;
		private final Packet<? extends Opcode> packet;
		
		public QueueItem(S session,Packet<? extends Opcode> packet)
		{
			this.session = session;
			this.packet = packet;
		}
		
		public S getSession()
		{
			return session;
		}
		
		public Packet<? extends Opcode> getPacket()
		{
			return packet;
		}
		
		@Override
		public String toString()
		{
			return ReflectionToStringBuilder.toString(this);
		}
	}
}
