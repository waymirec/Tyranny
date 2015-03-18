package net.waymire.tyranny.logging;

import java.util.logging.LogRecord;

public class TimestampFormatter extends BasicFormatter {

	@Override
	public String format(LogRecord record)
	{		
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS",record.getMillis())).append("] ");
		sb.append(super.format(record));
		return sb.toString();
	}
}
