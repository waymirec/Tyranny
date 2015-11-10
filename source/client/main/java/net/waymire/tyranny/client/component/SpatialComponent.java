package net.waymire.tyranny.client.component;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

public class SpatialComponent extends AbstractAppState implements GameComponent 
{
	private final Node rootNode;
	private final Node node;
	
	public SpatialComponent(AssetManager assetManager, Node rootNode)
	{
		this.rootNode = rootNode;
		this.node = new Node();
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		this.rootNode.attachChild(node);
	}

	@Override
	public void update(float tpf) 
	{

	}
	
	public Node getNode()
	{
		return node;
	}
}
