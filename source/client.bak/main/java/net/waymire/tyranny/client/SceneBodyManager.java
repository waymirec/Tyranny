package net.waymire.tyranny.client;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

public class SceneBodyManager extends AbstractPhysicBodyContext 
{
	private final ScenePhysicBodyContext scenePhysicBodyContext;
	private final SceneSpatialBodyContext sceneSpatialBodyContext;
	
	public SceneBodyManager(AppStateManager stateManager, AssetManager am, Node rootNode)
	{
		this.sceneSpatialBodyContext = new SceneSpatialBodyContext(am, rootNode);
		this.scenePhysicBodyContext = new ScenePhysicBodyContext(sceneSpatialBodyContext.getScene());
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		// PhysicsSpace initialization
		attachBulletAppState(stateManager);
		stateManager.attach(this.sceneSpatialBodyContext);
		stateManager.attach(this.scenePhysicBodyContext);
	}
}
