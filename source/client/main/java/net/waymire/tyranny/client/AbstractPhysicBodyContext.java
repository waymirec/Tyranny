package net.waymire.tyranny.client;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;

public abstract class AbstractPhysicBodyContext extends AbstractAppState 
{
	@SuppressWarnings("unused")
	private AppStateManager stateManager = null;

	private static final BulletAppState bulletAppState;
	static
	{
		bulletAppState = new BulletAppState();
	}
	
	public AbstractPhysicBodyContext()
	{
		
	}
	
	public BulletAppState getBulletAppState()
	{
		return bulletAppState;
	}

	/**
	 * Attaching BulletAppState to Initialize PhysicsSpace
	 * 
	 * @param stateManager the state manager to set
	 */
	public void attachBulletAppState(AppStateManager stateManager)
	{
		this.stateManager = stateManager;
		stateManager.attach(bulletAppState);
	}
}
