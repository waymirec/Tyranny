package net.waymire.tyranny.common.delegate;

import java.lang.reflect.Method;

public final class DelegateFactory {
	private DelegateFactory() { }
	
	public static Delegate createDelegate(Object target,String methodName)
	{
		return new DelegateImpl(target,methodName);
	}
	
	public static Delegate createDelegate(Object target,String methodName,Class<?>[] argTypes)
	{
		return new DelegateImpl(target,methodName,argTypes);
	}
	
	public static Delegate createDelegate(Object target, Method method, Class<?>[] argTypes)
	{
		return new DelegateImpl(target,method,argTypes);
	}
	
	public static Delegate createDelegate(Object target, Method method)
	{
		return new DelegateImpl(target,method);
	}
}
