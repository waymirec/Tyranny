package net.waymire.tyranny.client;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.system.AppSettings;

public class CameraContext extends AbstractAppState 
{
	private final AppSettings settings;
	private final InputManager inputManager;
	private final Camera cam;
	private final ChaseCamera chaseCam;
	private final FlyByCamera flyByCam;
	
  public CameraContext(AppSettings settings, InputManager inputManager, Camera cam, FlyByCamera flyByCam)
  {
	  this.settings = settings;
	  this.inputManager = inputManager;
	  this.cam = cam;
	  this.flyByCam = flyByCam;
	  this.chaseCam = new ChaseCamera(cam,inputManager);
  }
  
  @Override
  public void initialize(AppStateManager stateManager, Application app)
  {
	  super.initialize(stateManager, app);
	  //TODO: initialize your AppState, e.g. attach spatials to rootNode
      //this is called on the OpenGL thread after the AppState has been attached
	  
	  this.cam.setFrustumPerspective(116.0f, (settings.getWidth() / settings.getHeight()), 1.0f, 2000.0f);
	  this.flyByCam.setEnabled(false);
  }
  
  public Camera getCam()
  {
      return cam;
  }
  
  public ChaseCamera getChaseCam()
  {
	  return chaseCam;
  }
}
