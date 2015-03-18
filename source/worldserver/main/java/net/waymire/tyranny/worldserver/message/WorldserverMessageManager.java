package net.waymire.tyranny.worldserver.message;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.AutoStartable;
import net.waymire.tyranny.common.dynload.Autoload;
import net.waymire.tyranny.common.message.ChainableMessageManager;
import net.waymire.tyranny.common.message.MessageManager;

@Autoload(priority=5)
public class WorldserverMessageManager extends ChainableMessageManager implements AutoInitializable, AutoStartable
{

	public WorldserverMessageManager()
	{
		super();
	}
	
	public WorldserverMessageManager(int capacity)
	{
		super(capacity);
	}
	
	@Override
	public void autoInitialize()
	{
		this.setCapacity(1000);
		AppRegistry.getInstance().register(MessageManager.class, this);
	}
	
	@Override
	public void autoDeinitialize()
	{
		AppRegistry.getInstance().unregister(MessageManager.class);
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
