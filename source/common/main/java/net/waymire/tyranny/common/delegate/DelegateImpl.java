package net.waymire.tyranny.common.delegate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.waymire.tyranny.common.util.ClassUtil;

public class DelegateImpl implements Delegate {
	
	private static final Class<?>[] DEFAULT_ARG_TYPES = { Object[].class };
	
	private final Object target;
	private final Method method;
	
	public DelegateImpl(Object target,String methodName,Class<?>[] argTypes)
	{
		Class<?> c;
		c = (target instanceof Class<?>) ? (Class<?>)target : target.getClass();
		this.target = target;
		this.method = ClassUtil.getMethod(c, methodName, argTypes);
		
		if(this.method == null)
		{
			throw new DelegateCreationException(String.format("no such method: %s.%s()", ((Class<?>)target).getName(),methodName));
		}
		this.method.setAccessible(true);
	}
	
	public DelegateImpl(Object target,String methodName)
	{
		this(target,methodName,DEFAULT_ARG_TYPES);
	}
	
	public DelegateImpl(Object target, Method method)
	{
		this(target,method,DEFAULT_ARG_TYPES);
	}
	
	public DelegateImpl(Object target, Method method,Class<?>[] argTypes)
	{
		if(method == null)
		{
			throw new DelegateCreationException("method must not be null.");
		}
		
		this.target = target;
		this.method = method;
		this.method.setAccessible(true);
	}
	
	@Override
	public Object invoke(Object... args)
	{
		try
		{
			if(target instanceof Class<?>)
			{
				// Delegate target is a Class object
				return method.invoke(null,args);
			}
			else
			{
				// Delegate target is an instance
				return method.invoke(target,args);
			}
		}
		catch (IllegalAccessException e)
		{
			throw new DelegateInvocationException(e);
		}
		catch (IllegalArgumentException iae)
		{
			throw new DelegateInvocationException(iae);
		}
		catch (InvocationTargetException e)
		{
			Throwable t = e.getTargetException();
			
			if (t instanceof Error)
			{
				throw (Error) t;
			}
			
			if (t instanceof RuntimeException)
			{
				throw (RuntimeException) t;
			}
			
			throw new DelegateInvocationException(e);
		}
	}
	
	@Override
	public Object getTarget()
	{
		return target;
	}
	
	@Override
	public Method getMethod()
	{
		return method;
	}
	
	@Override
	public String toString()
	{
		String classname = target instanceof Class<?> ? ((Class<?>)target).getName() : target.getClass().getName();
		return classname + "." + method.getName() + "()";		
	}
	
	@Override
	public int hashCode()
	{
		return this.target.hashCode() + this.method.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{		
		if(o instanceof DelegateImpl)
		{
			DelegateImpl other = (DelegateImpl)o;
			Object t1 = this.target;
			Object t2 = other.getTarget();
			
			if(t1 == t2)
			{
				if(this.method.equals(other.getMethod()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
