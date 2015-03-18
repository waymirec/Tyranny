package net.waymire.tyranny.client.task;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.AutoStartable;
import net.waymire.tyranny.common.dynload.Autoload;
import net.waymire.tyranny.common.task.TaskManager;
import net.waymire.tyranny.common.task.TrackedTaskManager;

@Autoload(priority=1)
public class ClientTaskManager extends TrackedTaskManager implements AutoInitializable, AutoStartable
{
	public ClientTaskManager()
	{
		super();
	}
	
	public ClientTaskManager(int capacity)
	{
		super(capacity);
	}
	
	@Override
	public void autoInitialize()
	{
		this.setCapacity(1000);
		this.setMaxExecutionTime(3000);
		AppRegistry.getInstance().register(TaskManager.class, this);
	}
	
	@Override
	public void autoDeinitialize()
	{
		AppRegistry.getInstance().unregister(TaskManager.class);
	}
	
	@Override
	public void autoStart()
	{
		this.start();
	}
	
	@Override
	public void autoStop()
	{
		this.stop();
	}
}
