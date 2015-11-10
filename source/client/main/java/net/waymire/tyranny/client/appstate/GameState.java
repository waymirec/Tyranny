package net.waymire.tyranny.client.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import net.waymire.tyranny.client.GameClient;

public abstract class GameState extends AbstractAppState
{
	protected GameClient gameClient;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) 
	{
		super.initialize(stateManager, app);
		this.gameClient = (GameClient) app;
		gameClient.getInputManager().setCursorVisible(true);
	}
	
	@Override
	public void cleanup() 
	{
		super.cleanup();
	}
	
	@Override
    public void stateAttached(AppStateManager stateManager) 
	{
    }

    @Override
    public void stateDetached(AppStateManager stateManager) 
    {
    }    
}
