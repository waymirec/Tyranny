package net.waymire.tyranny.common.util;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.waymire.tyranny.common.ToString;

public class StringUtil {

	public static String toString(Object obj)
	{
		final Field fields[] = obj.getClass().getDeclaredFields();
		final List<String> candidates = new ArrayList<String>();
		
		for(Field field : fields)
		{
			if(field.getAnnotation(ToString.class) != null)
			{
				candidates.add(field.getName());
			}
		}
		
		try
		{
			StringBuilder sb = new StringBuilder();
			sb.append(obj.getClass().getName()).append("[");
			for(String memberName : candidates)
			{
				Field f1 = obj.getClass().getDeclaredField(memberName);
				f1.setAccessible(true);
				sb.append(memberName).append("=").append(f1.get(obj).toString()).append(",");
			}
			sb.append("]");
			return sb.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}		
	}
	
	public static String concat(Character delimeter,String...strings)
	{
		StringBuffer sb =  new StringBuffer();
		for(int i=0;i<strings.length; i++)
		{
			sb.append(strings[i]);
			if(delimeter != null)
			{
				sb.append(delimeter);
			}
		}
		return sb.toString();
	}
	
	public static String concat(String...strings)
	{
		return concat(null,strings);
	}
	
	public static boolean isValidString(String s)
	{
		return ((s != null) && (s.length() > 0));
	}
	
	public static String safeToString(Object o)
	{
		return (o == null) ? "" : o.toString();
	}
	
	public static boolean empty(String string)
	{
		return ((string == null) || (string.length() == 0));
	}
}
