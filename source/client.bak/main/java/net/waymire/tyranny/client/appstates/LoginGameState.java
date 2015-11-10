package net.waymire.tyranny.client.appstates;

import net.waymire.tyranny.client.NiftyConstants;
import net.waymire.tyranny.client.net.LoginserverClient;
import net.waymire.tyranny.client.ui.NiftyManager;
import net.waymire.tyranny.common.AppRegistry;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;

import de.lessvoid.nifty.elements.render.TextRenderer;

public class LoginGameState extends GameState 
{
	private LoginserverClient loginClient;
	
	private TextRenderer status;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) 
	{
		super.initialize(stateManager, app);
		this.loginClient = new LoginserverClient();
	}

	@Override
    public void stateAttached(AppStateManager stateManager) 
	{
		AppRegistry.getInstance().retrieve(NiftyManager.class).gotoScreen(NiftyConstants.LOGIN_SCREEN);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) 
    {
    	AppRegistry.getInstance().retrieve(NiftyManager.class).getNifty().exit();
    }    
}
