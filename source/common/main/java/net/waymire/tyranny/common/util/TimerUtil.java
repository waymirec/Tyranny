package net.waymire.tyranny.common.util;

public class TimerUtil 
{
	private TimerUtil() { }
	
	public static void sleep(long milliseconds)
	{
		long startTime = System.currentTimeMillis();
		long sleepFor = milliseconds;
		try {
			Thread.sleep(sleepFor); 
		} 
		catch (InterruptedException interruptedException) 
		{ 
			long remaining = sleepFor - (System.currentTimeMillis() - startTime);
			if(remaining > 10)
			{
				sleepFor = remaining;
			}
		}
	}
}