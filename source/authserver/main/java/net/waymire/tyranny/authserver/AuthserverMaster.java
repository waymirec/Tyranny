package net.waymire.tyranny.authserver;

import net.waymire.tyranny.authserver.configuration.AuthserverConfig;
import net.waymire.tyranny.authserver.configuration.ConfigurationFactory;
import net.waymire.tyranny.authserver.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.annotation.Locked;
import net.waymire.tyranny.common.annotation.Locked.LockMode;
import net.waymire.tyranny.common.annotation.Trace;
import net.waymire.tyranny.common.dynload.Autoloader;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessagePriority;
import net.waymire.tyranny.common.message.StandardMessage;

public class AuthserverMaster 
{
	private final String libraryPath;
	
	public static void main(String[] args)
	{
		AuthserverMaster master = new AuthserverMaster();
		master.startup();
	}
	
	public AuthserverMaster()
	{
		AuthserverEnvironment env = AppRegistry.getInstance().retrieve(AuthserverEnvironment.class);
		this.libraryPath = env.getFullLibraryPath();
	}
	
	@Trace
	@Locked(mode=LockMode.WRITE)
	public void startup()
	{
		LogHelper.info(this, "Loading configuration...");
		AuthserverConfig config = ConfigurationFactory.getAuthserverConfiguration();
		AppRegistry.getInstance().register(AuthserverConfig.class, config);
		
		LogHelper.info(this, "Auto loading classes...");
		Autoloader.getInstance().load(libraryPath);
		
		LogHelper.info(this, "Announcing That System Is Now Starting.");
		Message starting = new StandardMessage(this, MessageTopics.SYSTEM_STARTING);
		starting.setPriority(MessagePriority.URGENT);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(starting);
	}
	
	@Trace
	@Locked(mode=LockMode.WRITE)
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
