package net.waymire.tyranny.common.configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.waymire.tyranny.common.util.ClassUtil;

public class PropertiesConfiguration<T extends PropertyConfigKey>
{
	
	private final Map<T,String> values = new HashMap<>();
	
	protected PropertiesConfiguration()
	{
		
	}
	
	public void clear()
	{
		values.clear();
	}
	
	public void setValue(T key, String value)
	{
		values.put(key,value);
	}
	
	public String getValue(T key)
	{
		return values.get(key);
	}

	public <T2> T2 getValueAs(Class<T2> clazz, T key)
	{
		Method m = ClassUtil.getMethod(clazz, "valueOf", String.class);
		try {
			@SuppressWarnings("unchecked")
			T2 value = (T2)m.invoke(null, getValue(key));
			return value;
		} catch (Exception e) {
			return null;
		}
	}
}
