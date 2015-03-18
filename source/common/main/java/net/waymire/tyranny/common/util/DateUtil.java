package net.waymire.tyranny.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static final DateFormat m_dateformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	public static final DateFormat getDateFormat()
	{
		return m_dateformat;
	}
	
	public static final DateFormat getDateFormat(String format)
	{
		return new SimpleDateFormat(format);
	}
	
	public static final Date getTimestamp()
	{
		return new Date();
	}
	
	public static final String getTimestampAsString()
	{
		return m_dateformat.format(new Date());
	}
		
	public static final String getTimestampAsString(SimpleDateFormat format)
	{
		return format.format(new Date());
	}
	
	public static final String getTimestampAsString(String format)
	{
		return new SimpleDateFormat(format).format(new Date());
	}
	
	public static final String getTimestampAsString(Date date)
	{
		return m_dateformat.format(date);
	}

	public static final String getTimestampAsString(Date date,String format)
	{
		return new SimpleDateFormat(format).format(date);
	}
	
	public static final String format(Date date)
	{
		return m_dateformat.format(date);
	}
	
	public static final String format(Date date,String format)
	{
		return new SimpleDateFormat(format).format(date);
	}
}
