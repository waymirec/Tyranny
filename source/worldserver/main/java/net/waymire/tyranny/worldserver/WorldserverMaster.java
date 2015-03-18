package net.waymire.tyranny.worldserver;

import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.dynload.Autoloader;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessagePriority;
import net.waymire.tyranny.common.message.StandardMessage;
import net.waymire.tyranny.worldserver.configuration.ConfigurationFactory;
import net.waymire.tyranny.worldserver.configuration.WorldserverConfig;
import net.waymire.tyranny.worldserver.message.MessageTopics;

public class WorldserverMaster {
	private final String libraryPath;
	
	public static void main(String[] args)
	{
		WorldserverMaster master = new WorldserverMaster();
		master.startup();
	}
	
	public WorldserverMaster()
	{
		WorldserverEnvironment env = AppRegistry.getInstance().retrieve(WorldserverEnvironment.class);
		this.libraryPath = env.getFullLibraryPath();
	}
	
	public void startup()
	{
		LogHelper.info(this, "Loading configuration...");
		WorldserverConfig config = ConfigurationFactory.getWorldserverConfiguration();
		AppRegistry.getInstance().register(WorldserverConfig.class, config);
		
		LogHelper.info(this, "Auto loading classes...");
		Autoloader.getInstance().load(libraryPath);
		
		LogHelper.info(this, "Announcing That System Is Now Starting.");
		Message starting = new StandardMessage(this, MessageTopics.SYSTEM_STARTING);
		starting.setPriority(MessagePriority.URGENT);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(starting);
	}
	
	public void shutdown()
	{
		LogHelper.info(this, "Announcing That System Is Now Stopping.");
		Message stopping = new StandardMessage(this, MessageTopics.SYSTEM_STOPPING);
		stopping.setPriority(MessagePriority.URGENT);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(stopping);
		
		LogHelper.info(this, "Auto unloading classes...");
		Autoloader.getInstance().unload();
	}
}
