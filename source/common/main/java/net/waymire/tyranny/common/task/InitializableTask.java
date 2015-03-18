package net.waymire.tyranny.common.task;

public class InitializableTask extends StandardTask
{
	private boolean initialized = false;
	
	@Override
	public void run()
	{
		if(!initialized)
		{
			_initialize();
		}
		super.execute();
	}
	
	protected void initialize()
	{

	}
	
	private void _initialize()
	{
		initialize();
		initialized = true;
	}
}
