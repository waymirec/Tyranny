package net.waymire.tyranny.common.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.waymire.tyranny.common.PropertyRegistry;

public class VirtualBeanProxy {
	
    @SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> clazz)
    {
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz,VirtualBean.class}, new CustomInvocationHandler(clazz));
    }

    private static class CustomInvocationHandler implements InvocationHandler {
    	private final Class<?> targetClass;
    	private final PropertyRegistry<String> registry = new PropertyRegistry<String>();
    	private final Map<String,Date> changed = new HashMap<String,Date>();
    	private final Map<String,Date> dirty = new HashMap<String,Date>();
    	
    	public CustomInvocationHandler(Class<?> clazz)
    	{
    		this.targetClass = clazz;
    	}
    	
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            try {
            	if(method.getDeclaringClass().equals(VirtualBean.class))
            	{
            		Method m = this.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
            		m.setAccessible(true);
            		return m.invoke(this,args);
            	}
            	
            	String methodName = method.getName();
            	if(methodName.startsWith("get"))
                {
            		String tag = methodName.substring(3).toUpperCase();
            		return getProperty(tag);
                }
                else if(methodName.startsWith("set"))
                {
                	String tag = methodName.substring(3).toUpperCase();
                	setProperty(tag,args[0]);
                	return proxy;
                }
            	else
            	{
            		System.out.printf("invoking: %s\n", method.getName());
            		return method.invoke(this,args);
            		//throw new UnsupportedOperationException(method.getName());
            	}
            } finally {
            }
        }
        
        private void setProperty(String tag, Object value)
        {
        	String key = tag.toUpperCase();
        	Date now = new Date();
        	dirty.put(key, now);
        	changed.put(key,now);
        	registry.set(key, value);
        }
        
        private Object getProperty(String tag)
        {
        	String key = tag.toUpperCase();
    		dirty.remove(key);
    		return registry.get(key);
        }
        
        @SuppressWarnings("unused")
		private boolean isDirty(String tag)
    	{
    		return dirty.containsKey(tag.toUpperCase());
    	}
    	
        @SuppressWarnings("unused")
    	private boolean hasChanged(String tag)
    	{
    		return changed.containsKey(tag.toUpperCase());
    	}
    	
    	private List<String> getDirtyProperties()
    	{
    		List<String> list = new ArrayList<String>();
    		list.addAll(dirty.keySet());
    		return list;
    	}
    	
        @SuppressWarnings("unused")
    	private List<Method> getDirtyPropertyMethods()
    	{
    		List<Method> list = new ArrayList<Method>();
    		
    		List<String> props = getDirtyProperties();
    		Set<Method> methods = new HashSet<Method>();
    		methods.addAll(Arrays.asList(targetClass.getMethods()));
    		methods.addAll(Arrays.asList(targetClass.getDeclaredMethods()));
    		
    		for(Method method : methods)
    		{
    			if(props.size() == 0)
    			{
    				break;
    			}
    			
    			if(props.remove(method.getName().toUpperCase()))
    			{
    				list.add(method);
    			}
    		}
    		
    		return list;
    	}
    }
}
