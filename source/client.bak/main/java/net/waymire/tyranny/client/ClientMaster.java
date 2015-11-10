package net.waymire.tyranny.client;

import com.jme3.system.AppSettings;

import net.waymire.tyranny.client.configuration.ClientConfig;
import net.waymire.tyranny.client.configuration.ConfigurationFactory;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.dynload.Autoloader;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessagePriority;
import net.waymire.tyranny.common.message.StandardMessage;

public class ClientMaster 
{
	private final String libraryPath;
	
	public static void main(String[] args)
	{
		ClientMaster master = new ClientMaster();
		master.startup();
	}
	
	private ClientMaster()
	{
		ClientEnvironment env = AppRegistry.getInstance().retrieve(ClientEnvironment.class);
		this.libraryPath = env.getFullLibraryPath();
	}
	
	public void startup()
	{
		LogHelper.info(this, "Loading configuration...");
		ClientConfig config = ConfigurationFactory.getClientConfiguration();
		AppRegistry.getInstance().register(AppRegistryKeys.CLIENT_CONFIG, config);
		
		LogHelper.info(this, "Auto loading classes...");
		Autoloader.getInstance().load(libraryPath);
		
		LogHelper.info(this, "Announcing That System Is Now Starting.");
		Message starting = new StandardMessage(this, MessageTopics.SYSTEM_STARTING);
		starting.setPriority(MessagePriority.URGENT);
		AppRegistry.getInstance().retrieve(MessageManager.class).publish(starting);
		
		AppSettings settings = new AppSettings(true);
		settings.setTitle("Tyranny");
		
		MainGameClient client = new MainGameClient();
		client.setSettings(settings);
		client.setShowSettings(true);
		client.setPauseOnLostFocus(false);
		client.start();
	}
}
