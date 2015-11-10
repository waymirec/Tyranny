package net.waymire.tyranny.common.protocol;

import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import net.waymire.tyranny.common.delegate.DelegateImpl;
import net.waymire.tyranny.common.net.TcpSession;

public class ProtocolProcessorDelegate<T,P extends Packet<? extends Opcode>> extends DelegateImpl implements ProtocolProcessor<T,P> {
	
	private static final Class<?>[] argTypes = { TcpSession.class,Packet.class };
	
	public ProtocolProcessorDelegate(Object source,String methodName) {
		super(source,methodName,argTypes);
	}

	public ProtocolProcessorDelegate(Object source, String methodName, Class<P> clazz)
	{
		super(source,methodName, new Class<?>[] { TcpSession.class,clazz });
	}
	
	public ProtocolProcessorDelegate(Object source,Method method) {
		super(source,method,argTypes);
	}
	
	public ProtocolProcessorDelegate(Object source, Method method, Class<P> clazz)
	{
		super(source,method, new Class<?>[]{TcpSession.class, clazz});
	}
		
	protected ProtocolProcessorDelegate(Object source,String methodName, Class<?>[] argTypes)
	{
		super(source,methodName,argTypes);
	}
	
	protected ProtocolProcessorDelegate(Object source,Method method, Class<?>[] argTypes)
	{
		super(source,method,argTypes);
	}
	
	@Override
	public void process(T source,P packet) 
	{
		invoke(source,packet);
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return super.equals(o);
	}
}