package net.waymire.tyranny.client;

import net.waymire.tyranny.client.appstates.LoginGameState;
import net.waymire.tyranny.client.appstates.MainGameState;
import net.waymire.tyranny.client.message.MessageTopics;
import net.waymire.tyranny.client.ui.NiftyManager;
import net.waymire.tyranny.common.AppRegistry;
import net.waymire.tyranny.common.message.Message;
import net.waymire.tyranny.common.message.MessageManager;
import net.waymire.tyranny.common.message.MessageProcessor;

import com.jme3.app.SimpleApplication;

public class MainGameClient extends SimpleApplication 
{

	public MainGameClient()
	{
		AppRegistry.getInstance().retrieve(MessageManager.class).load(this);
	}
	
	@Override
	public void simpleInitApp() 
	{
		initGameStateManager();
		initNiftyManager();	
		
		AppRegistry.getInstance().retrieve(GameStateManager.class).attach(LoginGameState.class);
	}
	
	@Override
	protected void finalize()
	{
		AppRegistry.getInstance().retrieve(MessageManager.class).unload(this);
	}
	
	private void initNiftyManager()
	{
		NiftyManager niftyManager = new NiftyManager(this);
		AppRegistry.getInstance().register(NiftyManager.class, niftyManager);
	}
	
	private void initGameStateManager()
	{
		GameStateManager gameStateManager = new GameStateManager(this);
		gameStateManager.register(LoginGameState.class, new LoginGameState());
		gameStateManager.register(MainGameState.class, new MainGameState(stateManager, assetManager, settings, inputManager, rootNode, cam, flyCam));
		
		AppRegistry.getInstance().register(GameStateManager.class,  gameStateManager);
	}

    @MessageProcessor(topic=MessageTopics.LOGINSERVER_CLIENT_AUTH_SUCCESS)
	private void onAuthSuccessMessage(Message message)
	{
    	GameStateManager gameStateManager = AppRegistry.getInstance().retrieve(GameStateManager.class);
    	gameStateManager.detach(LoginGameState.class);
    	gameStateManager.attach(MainGameState.class);
	}
}
