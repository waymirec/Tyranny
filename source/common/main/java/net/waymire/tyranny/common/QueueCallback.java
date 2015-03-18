package net.waymire.tyranny.common;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.delegate.DelegateImpl;

public class QueueCallback<T> extends DelegateImpl {
	
	public static void main(String[] args)
	{
		new QueueCallback<String>(QueueCallback.class,"toString");
	}
	
	public QueueCallback(Object source,String methodName) 
	{
		super(source,methodName,new Class<?>[0]);
		Class<?> c = this.getClass();
		c.getTypeParameters();
	}
	
	public void process(T object)
	{
		invoke(object);
	}
	
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
}
