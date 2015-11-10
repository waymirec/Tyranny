package net.waymire.tyranny.client;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class AvatarSpatialBodyContext extends AbstractSpatialBodyContext 
{
	private final Node rootNode;
	private final Node avatar;
	private final Spatial avatarMesh;
	private final Vector3f correction;
	public AvatarSpatialBodyContext(AssetManager assetManager, Node rootNode)
	{
		this.rootNode = rootNode;
		this.avatar = new Node();
		this.avatarMesh = assetManager.loadModel("/Models/Sinbad/Sinbad.mesh.xml");
		//this.correction = new Vector3f(0, AvatarConstants.COLLISION_SHAPE_CENTRAL_POINT - AvatarConstants.COLLISION_SHAPE_RADIUS, 0);
		this.correction = new Vector3f(0, 0, 0);
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		avatarMesh.setLocalScale(new Vector3f(1f, 1f, 1f));
		avatarMesh.setLocalTranslation(correction);
		avatar.attachChild(avatarMesh);
		rootNode.attachChild(avatar);
	}
	
	public Node getAvatar()
    {
        return avatar;
    }
	
	public Spatial getAvatarMesh()
    {
        return avatarMesh;
    }
}
