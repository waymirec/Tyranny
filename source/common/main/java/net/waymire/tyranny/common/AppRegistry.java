package net.waymire.tyranny.common;

public class AppRegistry extends Registry
{
	private static AppRegistry INSTANCE;
	
	public static AppRegistry getInstance()
	{
		if(INSTANCE == null)
		{
			synchronized(AppRegistry.class)
			{
				if(INSTANCE == null)
				{
					INSTANCE = new AppRegistry();
				}
			}
		}
		return INSTANCE;
	}
	
	private AppRegistry() { }
}
