package net.waymire.tyranny.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
	
	public static String getStackTrace(Throwable t)
	{
		PrintWriter pw = new PrintWriter(new StringWriter());
		t.printStackTrace(pw);
		return t.toString();
	}
	
	public static String getReason(Throwable t)
	{
		StringBuilder sb =  new StringBuilder();
		sb.append(t.getClass().getName());
		
		String msg = t.getMessage();
		if((msg != null) && (msg.length() > 0))
		{
			sb.append(": ").append(t.getMessage());
		}
		
		return sb.toString();
	}
}
