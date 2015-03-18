package net.waymire.tyranny.common.message;

import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.delegate.DelegateImpl;

public class MessageListenerDelegate extends DelegateImpl implements MessageListener
{
	private static final Class<?>[] argTypes = { Message.class };
	
	public MessageListenerDelegate(Object source,String methodName) 
	{
		super(source,methodName,argTypes);
	}
	
	public MessageListenerDelegate(Object source, Method method)
	{
		super(source,method,argTypes);
	}
	
	@Override
	public void onMessage(Message message)
	{
		invoke(message);
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}
