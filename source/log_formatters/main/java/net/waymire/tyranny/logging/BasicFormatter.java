package net.waymire.tyranny.logging;

import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BasicFormatter extends Formatter {
	public static final String NEWLINE = System.getProperty("line.separator");
	
	@Override
	public String format(LogRecord record)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			sb.append(MessageFormat.format(record.getMessage(),record.getParameters()));
			sb.append(NEWLINE);
			return sb.toString();
		}
		catch(Exception exception)
		{
			
			return record.getMessage();
		}
	}
}
