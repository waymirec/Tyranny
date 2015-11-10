package net.waymire.tyranny.client.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import net.waymire.tyranny.client.input.ThirdPersonCamera;


/**
 *  Manages a ThirdPersonCamera.  
 *
 */
public class ThirdPersonCameraAppState extends AbstractAppState 
{

    private Application app;
    private ThirdPersonCamera camera;

    public ThirdPersonCameraAppState() 
    {
    }    

    /**
     *  This is called by BaseGameClient during initialize().
     */
    public void setCamera( ThirdPersonCamera cam ) 
    {
    	this.camera = cam;
    }
    
    public ThirdPersonCamera getCamera()
    {
        return camera;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) 
    {
        super.initialize(stateManager, app);
        
        this.app = app;

        if (app.getInputManager() != null) 
        {
            if (camera == null) 
            {
                camera = new ThirdPersonCamera(app.getCamera());
            }
            
            camera.registerWithInput(app.getInputManager());            
        }               
    }
            
    @Override
    public void setEnabled(boolean enabled) 
    {
        super.setEnabled(enabled);
        
        camera.setEnabled(enabled);
    }
    
    @Override
    public void cleanup() 
    {
        super.cleanup();

        if (app.getInputManager() != null) 
        {        
            camera.unregisterInput();
        }        
    }
}