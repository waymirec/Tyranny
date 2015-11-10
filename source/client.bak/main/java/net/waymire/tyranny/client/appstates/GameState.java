package net.waymire.tyranny.client.appstates;

import net.waymire.tyranny.client.MainGameClient;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

public abstract class GameState extends AbstractAppState
{
	protected MainGameClient game;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) 
	{
		super.initialize(stateManager, app);
		this.game = (MainGameClient) app;
		game.getInputManager().setCursorVisible(true);
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
