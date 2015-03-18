package net.waymire.tyranny.common.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.AsyncQueue;
import net.waymire.tyranny.common.annotation.LockField;
import net.waymire.tyranny.common.annotation.Locked;
import net.waymire.tyranny.common.annotation.Locked.LockMode;
import net.waymire.tyranny.common.delegate.Delegate;
import net.waymire.tyranny.common.delegate.DelegateImpl;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.net.TcpSession;

public class TcpProtocolProcessorRegistry<P extends Packet<? extends Opcode>> implements ProtocolHandler<TcpSession,P>,ProtocolProcessorRegistry<TcpSession,P> 
{
	public static final int DEFAULT_CAPACITY = 1000;
	
	@LockField
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	private final Map<Opcode,List<ProtocolProcessor<TcpSession,P>>> handlers = new HashMap<Opcode,List<ProtocolProcessor<TcpSession,P>>>();
	private final AsyncQueue<QueueItem> queue;
	private final AtomicBoolean running = new AtomicBoolean(false);
	
	public TcpProtocolProcessorRegistry()
	{
		this(DEFAULT_CAPACITY);
	}
	
	public TcpProtocolProcessorRegistry(int capacity)
	{
		Delegate d = new DelegateImpl(this,"execute", new Class<?>[]{ QueueItem.class });
		this.queue = new AsyncQueue<QueueItem>(d, capacity);		
	}
	
	@Override
	@Locked(mode=LockMode.WRITE)
	public void start()
	{
		if(!running.get())
		{
			queue.start();
		}
		else
		{
			throw new IllegalStateException("service is already running.");
		}
	}
	
	@Override
	@Locked(mode=LockMode.WRITE)
	public void stop()
	{
		if(running.get())
		{
			queue.stop();
			handlers.clear();
		}
		else
		{
			throw new IllegalStateException("service is not running.");
		}
	}
	
	@Override
	@Locked(mode=LockMode.WRITE)
	public void register(Opcode opcode,ProtocolProcessor<TcpSession,P> processor)
	{
		if(!handlers.containsKey(opcode))
		{
			handlers.put(opcode, new ArrayList<ProtocolProcessor<TcpSession,P>>());
		}
		if(!handlers.get(opcode).contains(processor))
		{
			handlers.get(opcode).add(processor);
		}
	}
	
	@Override
	@Locked(mode=LockMode.WRITE)
	public void deregister(Opcode opcode)
	{
		if(handlers.containsKey(opcode))
		{
			handlers.remove(opcode);
		}
	}
	
	@Override
	@Locked(mode=LockMode.WRITE)
	public void deregister(Opcode opcode,ProtocolProcessor<TcpSession,P> processor)
	{
		if(handlers.containsKey(opcode))
		{
			handlers.get(opcode).remove(processor);
		}
	}
	
	@Override
	@Locked(mode=LockMode.WRITE)
	public void handle(TcpSession session,P packet)
	{
		QueueItem item = new QueueItem(session,packet);
		queue.put(item);
	}
	
	@Override
	@Locked(mode=LockMode.READ)
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	@SuppressWarnings("unchecked")
	@Locked(mode=LockMode.READ)
	private void execute(final QueueItem item)
	{
		TcpSession session = item.getSession();
		P packet = (P)item.getPacket();
		Opcode opcode = packet.opcode();
		
		List<ProtocolProcessor<TcpSession,P>> list = handlers.get(opcode);
		if ((list != null) && !list.isEmpty()) 
		{
			for (ProtocolProcessor<TcpSession,P> handler : list) 
			{
				if (handler == null) 
				{
					LogHelper.info(this,  "Protocol handler is null. Deregistering...");
					deregister(opcode);
					return;
				}
				handler.process(session, packet);
			}
		}
	}
	
	private class QueueItem {
		private final TcpSession session;
		private final Packet<? extends Opcode> packet;
		
		public QueueItem(TcpSession session,Packet<? extends Opcode> packet)
		{
			this.session = session;
			this.packet = packet;
		}
		
		public TcpSession getSession()
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
