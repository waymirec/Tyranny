package net.waymire.tyranny.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassUtil
{
	public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass)
	{
		Annotation[] annotations = clazz.getAnnotations();
		for(Annotation annotation : annotations)
		{
			if(annotation.getClass() == annotationClass)
			{
				return true;
			}
		}
		return false;
	}
	
	public static Annotation getAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass)
	{
		List<Annotation> annotations = ClassUtil.getAnnotations(clazz);
		for(Annotation annotation : annotations)
		{
			if(annotation.annotationType() == annotationClass)
			{
				return annotation;
			}
		}
		return null;
	}
	
	public static List<Annotation> getAnnotations(Class<?> clazz)
	{
		List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.addAll(Arrays.asList(clazz.getAnnotations()));
		annotations.addAll(Arrays.asList(clazz.getDeclaredAnnotations()));
		return annotations;
	}
	
	public static <A extends Annotation> List<Field> getFieldsWithAnnotation(Class<?> targetClass, Class<A> annotationClass)
	{
		List<Field> ret = new ArrayList<Field>();
		List<Field> fields = ClassUtil.getAllClassFields(targetClass);
		for(Field f : fields)
		{
			if(f.getAnnotation(annotationClass) != null)
			{
				ret.add(f);
			}
		}
		return ret;
	}
	
	public static <A extends Annotation> List<String> getFieldNamesWithAnnotation(Class<?> targetClass, Class<A> annotationClass)
	{
		List<Field> fields = getFieldsWithAnnotation(targetClass, annotationClass);
		List<String> members = new ArrayList<String>();
		for(Field field : fields)
		{
			members.add(field.getName());
		}
		return members;
	}
	
	public static <A extends Annotation> List<Method> getMethodsWithAnnotation(Class<?> targetClass, Class<A> annotationClass)
	{
		List<Method> ret = new ArrayList<Method>();
		Method[] methods = ClassUtil.getAllClassMethods(targetClass);
		
		for(Method m : methods)
		{
			if(m.getAnnotation(annotationClass) != null)
			{
				ret.add(m);
			}
		}
		return ret;
	}
	
	public static <A extends Annotation> List<String> getMethodNamesWithAnnotation(Class<?> targetClass, Class<A> annotationClass)
	{
		List<Method> methods = getMethodsWithAnnotation(targetClass, annotationClass);
		List<String> members = new ArrayList<String>();
		for(Method method : methods)
		{
			members.add(method.getName());
		}
		return members;
	}
	
	public static Method[] getAllClassMethods(Class<?> type)
	{
		List<Method> methods = new ArrayList<Method>();
		for(Class<?> c = type; c != null; c = c.getSuperclass())
		{
			Method[] methodArray = c.getDeclaredMethods();
			methods.addAll(Arrays.asList(methodArray));
		}
		return methods.toArray(new Method[0]);
	}
	
	public static Method getMethod(Class<?> clazz, String methodName, Class<?>...params)
	{
		for(Class<?> c = clazz; c != null; c = c.getSuperclass())
		{
			for(Method m : c.getDeclaredMethods())
			{
				if(m.getName().equals(methodName))
				{
					Class<?>[] declaredParams = m.getParameterTypes();
					if(declaredParams.length == params.length)
					{
						boolean match = true;
						if(params.length > 0)
						{
							for(int i=0; i<params.length; i++)
							{
								if( ! params[i].equals(declaredParams[i]))
								{
									match = false;
									break;
								}
							}
						}
						if(match)
						{
							return m;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static List<Field> getAllClassFields(Class<?> type) 
	{
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> c = type; c != null; c = c.getSuperclass()) 
		{
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
		}
		return fields;
	}
	
	public static Class<?>[] getParameterizedTypes(Class<?> clazz)
	{
		List<Class<?>> types = new ArrayList<Class<?>>();
		
		Type base = clazz.getGenericSuperclass();
		if(base instanceof ParameterizedType)
		{ 
			for(Type type : ((ParameterizedType)base).getActualTypeArguments())
			{
				if(type instanceof Class)
				{
					types.add((Class<?>)type);
				}
				else if (type instanceof ParameterizedType)
				{
					types.add((Class<?>)((ParameterizedType)type).getRawType());
				}
			}
			return types.toArray(new Class<?>[0]);
		}
		else
		{
			for(Type intf : clazz.getGenericInterfaces())
			{
				ParameterizedType t = (ParameterizedType)intf;
				
				for (Type type : t.getActualTypeArguments())
				{
					if(type instanceof Class)
					{
						types.add((Class<?>)type);
					}
					else if (type instanceof ParameterizedType)
					{
						types.add((Class<?>)((ParameterizedType)type).getRawType());
					}
				}
			}
			return types.toArray(new Class<?>[0]);
		}		
	}
}
