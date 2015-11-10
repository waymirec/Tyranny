package net.waymire.tyranny.client.appstates;

import net.waymire.tyranny.client.AvatarBodyManager;
import net.waymire.tyranny.client.CameraContext;
import net.waymire.tyranny.client.SceneBodyManager;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public class MainGameState extends GameState 
{

	@SuppressWarnings("unused")
	private final Node rootNode;
	private final CameraContext sceneCameraContext;
	private final AvatarBodyManager avatarBodyManager;
	private final SceneBodyManager sceneBodyManager;
	
	public MainGameState(AppStateManager stateManager, AssetManager assetManager, AppSettings settings, InputManager inputManager, Node rootNode, Camera cam, FlyByCamera flyByCam)
	{
		this.rootNode = rootNode;
		this.sceneCameraContext = new CameraContext(settings, inputManager, cam, flyByCam);
		this.sceneBodyManager = new SceneBodyManager(stateManager, assetManager, rootNode);
		this.avatarBodyManager = new AvatarBodyManager(assetManager, rootNode, sceneCameraContext);
	}
	
	@Override
    public void initialize(AppStateManager stateManager, Application app)
    {
		//super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
		
		stateManager.attach(this.sceneCameraContext);
		stateManager.attach(this.sceneBodyManager); //initialize physic spacein constructor
		stateManager.attach(this.avatarBodyManager);
    }
	
	@Override
    public void update(float tpf)
    {
 
    }
}
