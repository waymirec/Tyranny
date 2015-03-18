package net.waymire.tyranny.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectUtil
{
	private ObjectUtil() { }

	public static Object getFieldValue(Field field, Object target)
	{
		try
		{
			field.setAccessible(true);
			return field.get(target);
		}
		catch(ReflectiveOperationException reflectiveOpEx)
		{
			throw new RuntimeException(reflectiveOpEx);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T shallowCopy(T source, String...ignoreArray)
	{
		List<String> ignoreList = new ArrayList<String>();
		if(ignoreArray.length > 0)
		{
			ignoreList = Arrays.asList(ignoreArray);
		}
		
		try
		{
			T clone = (T)source.getClass().newInstance();
			for(Field f : ClassUtil.getAllClassFields(source.getClass()))
			{
				if(ignoreList.contains(f.getName()) || Modifier.isStatic(f.getModifiers()))
				{
					continue;
				}
				
				f.setAccessible(true);
				if(Modifier.isFinal(f.getModifiers()))
				{
					Field modifiersField = Field.class.getDeclaredField("modifiers");
					modifiersField.setAccessible(true);
					modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
				}
				f.set(clone,  f.get(source));
			}
			return clone;
		}
		catch(ReflectiveOperationException reflectiveOpEx)
		{
			reflectiveOpEx.printStackTrace();
			return null;
		}
	}

	public static Map<String,Object> getAllNonNullMembers(Object o)
	{
		Map<String,Object> map = new HashMap<String,Object>();
		List<Field> fields = ClassUtil.getAllClassFields(o.getClass());
		for(Field field : fields)
		{
			try
			{
				field.setAccessible(true);
				Object value = field.get(o);
				if(value != null)
				{
					map.put(field.getName(), value);
				}
			}
			catch(Exception exception)
			{
			}
		}
		return map;
	}
	
	public static byte[] serialize(Object obj)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.close();
			baos.close();
			return baos.toByteArray();
		}
		catch(IOException ioException)
		{
			return null;
		}
	}
	
	public static Object deserialize(byte[] bytes)
	{
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object obj = ois.readObject();
			ois.close();
			bais.close();
			return obj;
		}
		catch(IOException|ClassNotFoundException exception)
		{
			exception.printStackTrace();
			return null;
		}
	}
}
