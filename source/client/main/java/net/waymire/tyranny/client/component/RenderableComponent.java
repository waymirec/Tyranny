package net.waymire.tyranny.client.component;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class RenderableComponent extends AbstractAppState implements GameComponent 
{
	private final Spatial avatarMesh;
	private final Node avatarNode;
	
	public RenderableComponent(AssetManager assetManager, Node rootNode, Node avatarNode)
	{
		this.avatarMesh = assetManager.loadModel("/Models/Oto/Oto.mesh.xml");
		this.avatarNode = avatarNode;
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		this.avatarMesh.setLocalScale(new Vector3f(1f, 1f, 1f));
		this.avatarMesh.setLocalTranslation(new Vector3f(0f,0f,0f));
		this.avatarNode.attachChild(avatarMesh);
	}

	@Override
	public void update(float tpf) 
	{

	}
	
	public Spatial getMesh()
	{
		return avatarMesh;
	}
}
