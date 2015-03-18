package net.waymire.tyranny.common.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

public class ProxyCreator {
	
	private ProxyCreator() { }

	public static <T> T create(Class<T> classs) throws Exception 
	{
		MethodHandler handler = new MethodHandler()
		{
			@Override
			public Object invoke(Object self, Method overridden,Method forwarder, Object[] args) throws Throwable
			{
				return forwarder.invoke(self, args);
			}
		};

		return create(classs,handler);
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> classs,MethodHandler handler) throws Exception
	{
		ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(classs);

		Class<?> clazz = factory.createClass();
		Object instance = clazz.newInstance();
		((ProxyObject) instance).setHandler(handler);
		return (T) instance;
	}
	
	public static <T> T create(Class<T> classs,Object...args) throws Exception
	{
		MethodHandler handler = new MethodHandler()
		{
			@Override
			public Object invoke(Object self, Method overridden,Method forwarder, Object[] args) throws Throwable
			{
				return forwarder.invoke(self, args);
			}
		};
		return create(classs,handler,args);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> classs,MethodHandler handler,Object...args) throws Exception
	{
		ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(classs);
		
		Class<?>[] paramTypes = new Class[args.length];
		for(int i=0; i<args.length; i++)
		{
			paramTypes[i] = args[i].getClass();
		}

		Class<T> proxyClass = factory.createClass();
		Constructor<T> ctor = proxyClass.getConstructor(paramTypes);
		T instance = ctor.newInstance(args);
		((ProxyObject) instance ).setHandler(handler);
		return instance;
	}
}