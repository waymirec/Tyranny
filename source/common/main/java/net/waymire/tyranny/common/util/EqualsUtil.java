package net.waymire.tyranny.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.waymire.tyranny.common.EqualsContributor;

public final class EqualsUtil {

	public static boolean equals(Object first,Object second)
	{
		if((first ==  null) || (second == null))
			return false;
		
		if(first == second)
			return true;

		try { first.getClass().cast(second); }
		catch (ClassCastException cce) { return false; }
		
		return equals(first,second,first.getClass());
	}
	
	public static boolean equals(Object first,Object second,Class<?> clazz)
	{
		if((first ==  null) || (second == null))
			return false;
		
		if(first == second)
			return true;

		try { first.getClass().cast(second); }
		catch (ClassCastException cce) { return false; }

		List<Field> fields = getAnnotatedFields(first, clazz);
		List<Method> methods = getAnnotatedMethods(first, clazz);
	
		if(!fields.isEmpty() || !methods.isEmpty())
		{
			boolean equals = true;
			equals &= getMemberEquality(first,second,fields);
			equals &= getMethodEquality(first,second,clazz,methods);
			return equals;
		}
		else
		{
			return first.equals(second);
		}
	}

	private static boolean getMemberEquality(Object first,Object second,List<Field> candidates)
	{
		try
		{
			boolean equals = true;
			for(Field field : candidates)
			{
				field.setAccessible(true);
				equals &= EqualsUtil.equals(field.get(first),field.get(second));
			}
			return equals;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}		
	}
	
	private static boolean getMethodEquality(Object first,Object second,Class<?> clazz,List<Method> candidates)
	{
		try
		{
			boolean equals = true;
			for(Method method : candidates)
			{
				equals &= EqualsUtil.equals(method.invoke(first),method.invoke(second));
			}
			return equals;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}		
	}
	
	private static List<Field> getAnnotatedFields(Object obj, Class<?> clazz)
	{
		final Set<Field> candidates = new HashSet<Field>();
		candidates.addAll(Arrays.asList(obj.getClass().getFields()));
		candidates.addAll(Arrays.asList(obj.getClass().getDeclaredFields()));

		final List<Field> fields = new ArrayList<Field>();
		for(Field field : candidates)
		{
			if(field.getAnnotation(EqualsContributor.class) != null)
			{
				fields.add(field);
			}
		}
		return fields;
	}
	
	private static List<Method> getAnnotatedMethods(Object obj, Class<?> clazz)
	{
		final Set<Method> candidates = new HashSet<Method>();
		candidates.addAll(Arrays.asList(clazz.getMethods()));
		candidates.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		
		final List<Method> methods = new ArrayList<Method>();
		for(Method method : candidates)
		{
			if(method.getAnnotation(EqualsContributor.class) != null)
			{
				if((method.getParameterTypes().length == 0) && (method.getReturnType() != null))
				{
					methods.add(method);
				}
			}
		}
		return methods;
	}
}
