package net.waymire.tyranny.client.component;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import net.waymire.tyranny.client.input.ThirdPersonCamera;

public class CameraComponent extends AbstractAppState implements GameComponent 
{
	private final Node avatar;
	private final ThirdPersonCamera thirdPersonCamera;
	
	public CameraComponent(Camera camera, InputManager inputManager, Node avatar)
	{
		this.thirdPersonCamera = new ThirdPersonCamera(camera);
		this.thirdPersonCamera.registerWithInput(inputManager);
		this.avatar = avatar;
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		thirdPersonCamera.setSpatial(avatar);
		thirdPersonCamera.setPhysicsSpace(stateManager.getState(BulletAppState.class).getPhysicsSpace());
		avatar.addControl(thirdPersonCamera);
	}
}
