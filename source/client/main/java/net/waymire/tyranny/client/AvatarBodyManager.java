package net.waymire.tyranny.client;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class AvatarBodyManager extends AbstractPhysicBodyContext 
{
	@SuppressWarnings("unused")
	private final Node rootNode;
	
	@SuppressWarnings("unused")
	private final CameraContext cameraContext;
	private final Camera cam;
	private final ChaseCamera chaseCam;
	private final ThirdPersonCamera thirdPersonCamera;
	
	private final AvatarPhysicBodyContext physicBodyContext;
	private final AvatarSpatialBodyContext spatialBodyContext;
	
	private final PhysicsCharacter physicBody;
	private final Node avatar;
	private final BetterCharacterControl characterControl;
	
	private final PlayerInputActionListener playerInputListener;
	
	@SuppressWarnings("unused")
	private InputManager inputManager;

	public AvatarBodyManager(AssetManager assetManager, Node rootNode, CameraContext cameraContext)
	{
		this.rootNode = rootNode;
		
		this.spatialBodyContext = new AvatarSpatialBodyContext(assetManager, rootNode);
		this.physicBodyContext = new AvatarPhysicBodyContext();
		
		this.physicBody = physicBodyContext.getPhysicBody();
		
		this.avatar = spatialBodyContext.getAvatar();
		this.characterControl = new BetterCharacterControl(AvatarConstants.COLLISION_SHAPE_RADIUS, AvatarConstants.COLLISION_SHAPE_RADIUS * 2, AvatarConstants.PHYSIC_BODY_MASS);

		this.playerInputListener = new PlayerInputActionListener(this.physicBody, this.spatialBodyContext.getAvatarMesh());
		
		this.cameraContext = cameraContext;
		this.cam = cameraContext.getCam();
		this.chaseCam = cameraContext.getChaseCam();
		
		this.thirdPersonCamera = cameraContext.getThirdPersonCamera();
		thirdPersonCamera.setSpatial(avatar);
		thirdPersonCamera.setPhysicsSpace(physicBodyContext.getBulletAppState().getPhysicsSpace());		
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		//TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
		
		stateManager.attach(this.spatialBodyContext);
		stateManager.attach(this.physicBodyContext);
		stateManager.attach(this.playerInputListener);
		
		this.avatar.addControl(new AvatarBodyMoveControl(playerInputListener, physicBody, cam));
		this.avatar.addControl(chaseCam);
		this.avatar.addControl(characterControl);
	}
	
	@Override
    public void update(float tpf)
    {
		
    }
}
