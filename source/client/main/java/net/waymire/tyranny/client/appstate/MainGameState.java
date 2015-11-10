package net.waymire.tyranny.client.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import net.waymire.tyranny.client.AvatarBodyManager;
import net.waymire.tyranny.client.CameraContext;
import net.waymire.tyranny.client.SceneBodyManager;

public class MainGameState extends GameState 
{

	@SuppressWarnings("unused")
	private final Node rootNode;
	private final CameraContext cameraContext;
	private final AvatarBodyManager avatarBodyManager;
	private final SceneBodyManager sceneBodyManager;
	
	public MainGameState(AppStateManager stateManager, AssetManager assetManager, AppSettings settings, InputManager inputManager, Node rootNode, Camera cam, FlyByCamera flyByCam)
	{
		this.rootNode = rootNode;
		this.cameraContext = new CameraContext(settings, inputManager, cam, flyByCam);
		this.sceneBodyManager = new SceneBodyManager(stateManager, assetManager, rootNode);
		this.avatarBodyManager = new AvatarBodyManager(assetManager, rootNode, cameraContext);
	}
	
	@Override
    public void initialize(AppStateManager stateManager, Application app)
    {
		//super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
		
		stateManager.attach(cameraContext);
		stateManager.attach(this.sceneBodyManager); //initialize physic spacein constructor
		stateManager.attach(this.avatarBodyManager);
    }
	
	@Override
    public void update(float tpf)
    {
 
    }
}
