package net.waymire.tyranny.common.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import net.waymire.tyranny.common.HashContributor;

/**
 * Collected methods which allow easy implementation of <code>hashCode</code>.
 * 
 * Example use case:
 * 
 * <pre>
 * public int hashCode()
 * {
 * 	int result = HashCodeUtil.SEED;
 * 	//collect the contributions of various fields
 * 	result = HashCodeUtil.hash(result, fPrimitive);
 * 	result = HashCodeUtil.hash(result, fObject);
 * 	result = HashCodeUtil.hash(result, fArray);
 * 	return result;
 * }
 * </pre>
 */
public final class HashCodeUtil {

	/**
	 * An initial value for a <code>hashCode</code>, to which is added
	 * contributions from fields. Using a non-zero value decreases collisons of
	 * <code>hashCode</code> values.
	 */
	public static final int SEED = 23;

	public static int hashObject(Object obj)
	{
		return hashObject(obj,obj.getClass());
	}
	
	public static int hashObject(Object obj, Class<?> clazz)
	{
		List<Field> fields = ClassUtil.getFieldsWithAnnotation(clazz, HashContributor.class);
		List<Method> methods = ClassUtil.getMethodsWithAnnotation(clazz, HashContributor.class);
		
		if(!fields.isEmpty() || !methods.isEmpty())
		{
			int hash = HashCodeUtil.SEED;
			if(!fields.isEmpty())
			{
				hash = HashCodeUtil.hashMembers(hash,obj,fields);
			}
			if(!methods.isEmpty())
			{
				hash = HashCodeUtil.hashMethods(hash,obj, clazz,methods);
			}
			return hash;
		}
		else
		{
			return obj.hashCode();
		}
	}
	
	/**
	 * booleans.
	 */
	public static int hash(int aSeed, boolean aBoolean)
	{
		return firstTerm(aSeed) + (aBoolean ? 1 : 0);
	}

	/**
	 * chars.
	 */
	public static int hash(int aSeed, char aChar)
	{
		return firstTerm(aSeed) + (int) aChar;
	}

	/**
	 * ints.
	 */
	public static int hash(int aSeed, int aInt)
	{
		/*
		 * Implementation Note Note that byte and short are handled by this
		 * method, through implicit conversion.
		 */
		return firstTerm(aSeed) + aInt;
	}

	/**
	 * longs.
	 */
	public static int hash(int aSeed, long aLong)
	{
		return firstTerm(aSeed) + (int) (aLong ^ (aLong >>> 32));
	}

	/**
	 * floats.
	 */
	public static int hash(int aSeed, float aFloat)
	{
		return hash(aSeed, Float.floatToIntBits(aFloat));
	}

	/**
	 * doubles.
	 */
	public static int hash(int aSeed, double aDouble)
	{
		return hash(aSeed, Double.doubleToLongBits(aDouble));
	}

	/**
	 * <code>aObject</code> is a possibly-null object field, and possibly an
	 * array.
	 * 
	 * If <code>aObject</code> is an array, then each element may be a primitive
	 * or a possibly-null object.
	 */
	public static int hash(int aSeed, Object aObject)
	{
		int result = aSeed;
		if (aObject == null)
		{
			result = hash(result, 0);
		} else if (!isArray(aObject))
		{
			result = hash(result, HashCodeUtil.hashObject(aObject));
		} else
		{
			int length = Array.getLength(aObject);
			for (int idx = 0; idx < length; ++idx)
			{
				Object item = Array.get(aObject, idx);
				// recursive call!
				result = hash(result, item);
			}
		}
		return result;
	}

	// / PRIVATE ///
	private static final int fODD_PRIME_NUMBER = 37;

	private static int firstTerm(int aSeed)
	{
		return fODD_PRIME_NUMBER * aSeed;
	}

	private static boolean isArray(Object aObject)
	{
		return aObject.getClass().isArray();
	}

	private static int hashMembers(int hash,Object obj,List<Field> candidates)
	{
		try
		{
			for(Field member : candidates)
			{
				member.setAccessible(true);
				hash = HashCodeUtil.hash(hash,member.get(obj));
			}
			return hash;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}	
	}
	
	private static int hashMethods(int hash, Object obj, Class<?> clazz,List<Method> candidates)
	{
		try
		{
			for(Method method : candidates)
			{
				hash = HashCodeUtil.hash(hash, method.invoke(obj));
			}
			return hash;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}		
	}	
}
