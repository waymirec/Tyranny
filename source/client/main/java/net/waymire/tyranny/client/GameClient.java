package net.waymire.tyranny.client;

import java.util.concurrent.Callable;

import net.waymire.tyranny.client.appstate.MainGameState;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.GUID;
import net.waymire.tyranny.common.dynload.AutoInitializable;
import net.waymire.tyranny.common.dynload.Autoload;
import net.waymire.tyranny.common.logging.LogHelper;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessageProcessor;
import net.waymire.tyranny.common.net.TcpSession;
import net.waymire.tyranny.common.protocol.WorldserverOpcode;
import net.waymire.tyranny.common.protocol.WorldserverPacket;

import com.jme3.system.AppSettings;

@Autoload(priority=201)
public class GameClient extends BaseGameClient implements AutoInitializable
{
	private MainGameState mainGameState;

	public GameClient()
	{
		AppRegistry.getInstance().register(GameClient.class, this);
	}
	
	@Override
	public void simpleInitApp()
	{
		mainGameState = new MainGameState(this.getStateManager(),this.getAssetManager(),this.settings,this.getInputManager(),this.getRootNode(),this.getCamera(), this.getFlyByCamera());		
		AppRegistry.getInstance().register(MainGameState.class, mainGameState);
	}

	@Override
	public void autoInitialize()
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.load(this);
	}
	
	@Override
	public void autoDeinitialize()
	{
		MessageManager messageManager = AppRegistry.getInstance().retrieve(MessageManager.class);
		messageManager.unload(this);
	}
	
	@MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTH_SUCCESS)
	private void onLoginAuthSuccess(Message message)
	{
		LogHelper.info(this, "DEBUG::LOGIN AUTH SUCCESS");
		AppSettings settings = new AppSettings(true);
		settings.setTitle("Tyranny");
		settings.setResolution(1280, 800);
		settings.setFullscreen(false);
		settings.setVSync(false);

		this.setSettings(settings);
		this.setShowSettings(true);
		this.setPauseOnLostFocus(false);
		this.start();

	}
	
	@MessageProcessor(topic=MessageTopics.WORLDSERVER_CLIENT_IDENT_SUCCESS)
	private void onWorldserverIdentSuccessMessage(Message message)
	{
		LogHelper.info(this, "DEBUG::WORLD IDENT SUCCESS");

		TcpSession session = (TcpSession)message.getSource();

		GUID characterId = GUID.generate();
		WorldserverPacket enterWorld = new WorldserverPacket(WorldserverOpcode.ENTER_WORLD_REQ);
		enterWorld.putLong(characterId.getMostSignificantBits());
		enterWorld.putLong(characterId.getLeastSignificantBits());
		enterWorld.prepare();
		session.send(enterWorld);
	}
	
	@MessageProcessor(topic=MessageTopics.ENTER_WORLD)
	private void onEnterWorldMessage(Message message)
	{
		LogHelper.info(this, "DEBUG::ENTERING WORLD.");
		this.enqueue(new Callable<Void>() {
			public Void call()
			{
				GameClient gameClient = AppRegistry.getInstance().retrieve(GameClient.class);
				MainGameState mainGameState =  AppRegistry.getInstance().retrieve(MainGameState.class);
				gameClient.getStateManager().attach(mainGameState);
				return null;
			}
		});
	}	
}
