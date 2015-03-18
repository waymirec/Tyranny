package net.waymire.tyranny.client;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;

public class ScenePhysicBodyContext extends AbstractPhysicBodyContext 
{
	private final RigidBodyControl rigidBodyControl;
	private final Node scene;
	
	public ScenePhysicBodyContext(Node scene)
	{
		this.scene = scene;
		this.rigidBodyControl = new RigidBodyControl(.0f);
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		// Add scene to PhysicsSpace
		scene.addControl(rigidBodyControl);
		getBulletAppState().getPhysicsSpace().addAll(scene);
	}
}
