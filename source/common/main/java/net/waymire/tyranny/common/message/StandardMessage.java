package net.waymire.tyranny.common.message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class StandardMessage implements Message {
	static final AtomicLong GLOBAL_COUNTER = new AtomicLong(0);
	
	private final Object source;
	private final String topic;
	private final Map<String,Object> properties = new HashMap<String,Object>();
	private final Long sequence;
	
	private MessagePriority priority;

	public StandardMessage(Object source,String topic)
	{
		this.source = source;
		this.topic = topic;
		this.priority = MessagePriority.NORMAL;
		this.sequence = GLOBAL_COUNTER.getAndIncrement();
	}
	
	@Override
	public Object getSource() 
	{
		return source;
	}

	@Override
	public String getTopic() 
	{
		return topic;
	}

	public Long getSequence()
	{
		return sequence;
	}
	
	public MessagePriority getPriority()
	{
		return priority;
	}
	
	public void setPriority(MessagePriority priority)
	{
		this.priority = priority;
	}
	
	@Override
	public void setProperty(String key, Object value)
	{
		properties.put(key, value);
	}
	
	@Override
	public Object getProperty(String key)
	{
		return properties.get(key);
	}	
}
