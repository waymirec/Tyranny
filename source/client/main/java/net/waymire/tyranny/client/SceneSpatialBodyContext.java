package net.waymire.tyranny.client;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class SceneSpatialBodyContext extends AbstractSpatialBodyContext 
{
	private final Node rootNode;
	private final Node scene;
	
	private AmbientLight ambient;
	private DirectionalLight sun;
	
	public SceneSpatialBodyContext(AssetManager am, Node rootNode)
	{
		this.rootNode = rootNode;
		am.registerLocator("/home/tyranny/source/client/main/assets/wildhouse.zip",  ZipLocator.class);
		this.scene = (Node)am.loadModel("main.scene");
		this.ambient = new AmbientLight();
		this.sun = new DirectionalLight();
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		// Main scene loading
		this.scene.setLocalScale(.1f);
		this.scene.scale(32.0f);
		this.sun.setDirection(new Vector3f(1.4f, -1.4f, -1.4f));
		//this.scene.setLocalTranslation(Vector3f.ZERO);
		this.scene.setLocalTranslation(new Vector3f(0, 1000, 0));
		
		rootNode.attachChild(this.scene);
		rootNode.addLight(this.ambient);
		rootNode.addLight(this.sun);
	}
	
	public Node getScene()
	{
		return scene;
	}
}
