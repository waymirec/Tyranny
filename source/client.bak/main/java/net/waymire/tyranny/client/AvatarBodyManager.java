package net.waymire.tyranny.client;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;

public class AvatarBodyManager extends AbstractPhysicBodyContext 
{
	@SuppressWarnings("unused")
	private final Node rootNode;
	
	private final CameraContext cameraContext;	
	private final AvatarPhysicBodyContext physicBodyContext;
	private final AvatarSpatialBodyContext spatialBodyContext;
	
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
		
		this.avatar = spatialBodyContext.getAvatar();
		this.characterControl = new BetterCharacterControl(AvatarConstants.COLLISION_SHAPE_RADIUS, AvatarConstants.COLLISION_SHAPE_RADIUS * 2, AvatarConstants.PHYSIC_BODY_MASS);

		this.playerInputListener = new PlayerInputActionListener(physicBodyContext.getPhysicBody(), spatialBodyContext.getAvatarMesh());
		
		this.cameraContext = cameraContext;
		cameraContext.getThirdPersonCamera().setSpatial(avatar);
		cameraContext.getThirdPersonCamera().setPhysicsSpace(physicBodyContext.getBulletAppState().getPhysicsSpace());		
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		//TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
		
		stateManager.attach(this.spatialBodyContext);
		stateManager.attach(this.physicBodyContext);
		stateManager.attach(this.playerInputListener);
		
		this.avatar.addControl(new AvatarBodyMoveControl(playerInputListener, physicBodyContext.getPhysicBody(), cameraContext.getCam()));
		cameraContext.getThirdPersonCamera().setSpatial(this.avatar);
		this.avatar.addControl(characterControl);
	}
	
	@Override
    public void update(float tpf)
    {
		
    }
}
