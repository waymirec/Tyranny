package net.waymire.tyranny.client.input;

import java.io.IOException;
import java.util.LinkedList;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

public class ThirdPersonCamera implements ActionListener, AnalogListener, Control 
{
    private static final String[] MAPPINGS = {"camToggleRotate", "camDown", "camUp", "camMouseLeft", "camMouseRight", "camZoomIn", "camZoomOut"};
    
	private InputManager inputManager;
    private Camera cam = null;
    private Spatial spatial = null;
    private Vector3f initialUpVec;
    private PhysicsSpace physicsSpace;
    private float minVerticalRotation = 0.00f;
    private float maxVerticalRotation = FastMath.PI / 2;
    private float minDistance = 1.0f;
    private float maxDistance = 40.0f;
    private float zoomSpeed = 2f;
    private float rotationSpeed = 1.0f;
    private float camRotation = 0;
    private float camVRotation = FastMath.PI / 6;
    private float camDistance = 20;
    private Quaternion qCamRotation = Quaternion.IDENTITY;
    private Vector3f rotatedCamDir = Vector3f.ZERO;
    private Vector3f playerDir = Vector3f.ZERO;
    private Vector3f camLoc = Vector3f.ZERO;
    private boolean canRotate;
    private boolean enabled = true;
    private LinkedList<PhysicsRayTestResult> collisionResults = null;
    private float noCollisionDistance = 0f;
    private Vector3f noCollisionLoc = Vector3f.ZERO;
    
    /**
     * Constructs the third person camera
     * @param cam the application camera
     * @param target the spatial to follow
     */
    public ThirdPersonCamera(Camera cam) 
    {
        this.cam = cam;
        initialUpVec = cam.getUp().clone();
        cam.setLocation(camLoc);
    }
    
    public Camera getCamera()
    {
    	return cam;
    }
    
    public void onAction(String name, boolean keyPressed, float tpf) 
    {
        if (name.equals("camToggleRotate") && enabled) 
        {
            if (keyPressed) 
            {
                canRotate = true;
                inputManager.setCursorVisible(false);
            }
            else 
            {
                canRotate = false;
                inputManager.setCursorVisible(true);
            }
        }
    }
    
    //change Mouse Axis here (swap the negations)
    public void onAnalog(String name, float value, float tpf) 
    {
        if (name.equals("camMouseLeft")) 
        {
            rotateCamera(value);
        } 
        else if (name.equals("camMouseRight")) 
        {
            rotateCamera(-value);
        } 
        else if (name.equals("camUp")) 
        {
            vRotateCamera(value);
        } 
        else if (name.equals("camDown")) 
        {
            vRotateCamera(-value);
        } 
        else if (name.equals("camZoomIn")) 
        {
            zoomCamera(value);
        } 
        else if (name.equals("camZoomOut")) 
        {
            zoomCamera(-value);
        }
    }
    
    /**
     * Registers inputs with the input manager
     * @param inputManager
     */
    public void registerWithInput(InputManager inputManager) 
    {
        this.inputManager = inputManager;
        inputManager.addMapping("camDown", new MouseAxisTrigger(1, true));
        inputManager.addMapping("camUp", new MouseAxisTrigger(1, false));
        inputManager.addMapping("camZoomIn", new MouseAxisTrigger(2, true));
        inputManager.addMapping("camZoomOut", new MouseAxisTrigger(2, false));
        inputManager.addMapping("camMouseLeft", new MouseAxisTrigger(0, true));
        inputManager.addMapping("camMouseRight", new MouseAxisTrigger(0, false));
        inputManager.addMapping("camToggleRotate", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("camToggleRotate", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(this, MAPPINGS);
    }
    
    public void unregisterInput()
    {
        if (inputManager == null) 
        {
            return;
        }
    
        for (String s : MAPPINGS) 
        {
            if (inputManager.hasMapping(s)) 
            {
                inputManager.deleteMapping( s );
            }
        }

        inputManager.removeListener(this);
        inputManager.setCursorVisible(true);
    }

    //computes the rotation of the relative camera, based on target rotation and
    //camera rotation
    private void computeRotation() 
    {
        qCamRotation = qCamRotation.fromAngleNormalAxis(camRotation, initialUpVec);
        
        if (playerDir != null) 
        {
            rotatedCamDir = qCamRotation.mult(playerDir).normalize();
        } 
        else 
        {
            rotatedCamDir = qCamRotation.mult(Vector3f.UNIT_XYZ);
            rotatedCamDir.y = 0;
            rotatedCamDir.normalizeLocal();
            rotatedCamDir.y = 0;
        }
        rotatedCamDir.addLocal(0, FastMath.sin(camVRotation), 0);
    }
    
    //computes the position of the camera
    private void computeCamPosition() 
    {
        camLoc = rotatedCamDir.mult(camDistance);
        camLoc.addLocal(spatial.getWorldTranslation());
    }
    
    //computes the position of the camera with no collision
    private void computeNoCollisionPosition() 
    {
        noCollisionLoc = rotatedCamDir.mult(noCollisionDistance);
        noCollisionLoc.addLocal(spatial.getWorldTranslation());
    }
    
    //check for collisions
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkCollisions() 
    {
        if (physicsSpace!=null) {
            collisionResults = (LinkedList) physicsSpace.rayTest(spatial.getWorldTranslation(), camLoc);
            float hitFraction = 1f;
            if (collisionResults != null && collisionResults.size() > 0) {
                hitFraction = collisionResults.getFirst().getHitFraction();
                noCollisionDistance = ((float) ((int) (hitFraction * 100))) / 100 * camDistance;
            } else {
                noCollisionDistance = camDistance;
            }
        } else {
            noCollisionDistance = camDistance;
        }
    }
    
    //rotate the camera around the target on the horizontal plane
    private void rotateCamera(float value) 
    {
        if (!canRotate || !enabled) 
        {
            return;
        }
        camRotation += value * rotationSpeed;
    }
    
    //move the camera toward or away the target
    private void zoomCamera(float value) 
    {
        if (!enabled) 
        {
            return;
        }
        
        camDistance += value * zoomSpeed;
        
        if (camDistance > maxDistance) 
        {
            camDistance = maxDistance;
        }
        
        if (camDistance < minDistance) 
        {
            camDistance = minDistance;
        }
        
        if ((camVRotation < minVerticalRotation) && (camDistance > (minDistance + 1.0f))) 
        {
            camVRotation = minVerticalRotation;
        }
    }
    
    //rotate the camera around the target on the vertical plane
    private void vRotateCamera(float value) 
    {
        if (!canRotate || !enabled) 
        {
            return;
        }
        
        camVRotation += value * rotationSpeed;
        
        if (camVRotation > maxVerticalRotation) 
        {
            camVRotation = maxVerticalRotation;
        }
        
        if ((camVRotation < minVerticalRotation) && (camDistance > (minDistance + 1.0f))) 
        {
            camVRotation = minVerticalRotation;
        }
    }
    
    /**
     * Updates the camera, should only be called internally
     */
    protected void updateCamera(float tpf) 
    {
        if (enabled) 
        {
            playerDir=getViewDirection();
            playerDir.y=0;
            computeRotation();
            computeCamPosition();
            checkCollisions();
            computeNoCollisionPosition();
            cam.setLocation(noCollisionLoc);
            cam.lookAt(spatial.getWorldTranslation(), initialUpVec);
        }
    }
    
    /**
     * Return the enabled/disabled state of the camera
     * @return true if the camera is enabled
     */
    public boolean isEnabled() 
    {
        return enabled;
    }
    
    /**
     * Enable or disable the camera
     * @param enabled true to enable
     */
    public void setEnabled(boolean enabled) 
    {
        this.enabled = enabled;
        if (!enabled) 
        {
            canRotate = false; // reset this flag in-case it was on before
        }
    }
    
    /**
     * Returns the max zoom distance of the camera (default is 40)
     * @return maxDistance
     */
    public float getMaxDistance() 
    {
        return maxDistance;
    }
    
    /**
     * Sets the max zoom distance of the camera (default is 40)
     * @param maxDistance
     */
    public void setMaxDistance(float maxDistance) 
    {
        this.maxDistance = maxDistance;
    }
    
    /**
     * Returns the min zoom distance of the camera (default is 1)
     * @return minDistance
     */
    public float getMinDistance() 
    {
        return minDistance;
    }
    
    /**
     * Sets the min zoom distance of the camera (default is 1)
     * @return minDistance
     */
    public void setMinDistance(float minDistance) 
    {
        this.minDistance = minDistance;
    }
    /**
     * clone this camera for a spatial
     * @param spatial
     * @return
     */
    public Control cloneForSpatial(Spatial spatial) 
    {
    	ThirdPersonCamera cc = new ThirdPersonCamera(cam);
    	cc.registerWithInput(inputManager);
        cc.setMaxDistance(getMaxDistance());
        cc.setMinDistance(getMinDistance());
        spatial.addControl(this);
        return cc;
    }
    
    /**
     * Sets the spacial for the camera control, should only be used internally
     * @param spatial
     */
    public void setSpatial(Spatial spatial) 
    {
    	this.spatial = spatial;
    	
    	if(spatial != null)
    	{
    		computeCamPosition();
    	}
    	else
    	{
    		this.spatial.removeControl(this);
    	}
    }
    
    public void setPhysicsSpace(PhysicsSpace physicsSpace)
    {
    	this.physicsSpace = physicsSpace;
    }
    
    private Vector3f getViewDirection() 
    {
    	return spatial.getControl(BetterCharacterControl.class).getViewDirection();
    }
    
    /**
     * update the camera control, should on ly be used internally
     * @param tpf
     */
    public void update(float tpf) {
        updateCamera(tpf);
    }
    /**
     * renders the camera control, should on ly be used internally
     * @param rm
     * @param vp
     */
    public void render(RenderManager rm, ViewPort vp) {
        //nothing to render
    }
    
    /**
     * Write the camera
     * @param ex the exporter
     * @throws IOException
     */
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(maxDistance, "maxDistance", 40);
        capsule.write(minDistance, "minDistance", 1);
    }
    /**
     * Read the camera
     * @param im
     * @throws IOException
     */
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        maxDistance = ic.readFloat("maxDistance", 40);
        minDistance = ic.readFloat("minDistance", 1);
    }
    
    /**
     * returns the maximal vertical rotation angle of the camera around the target
     * @return
     */
    public float getMaxVerticalRotation() 
    {
        return maxVerticalRotation;
    }
    
    /**
     * sets the maximal vertical rotation angle of the camera around the target default is Pi/2;
     * @param maxVerticalRotation
     */
    public void setMaxVerticalRotation(float maxVerticalRotation) 
    {
        this.maxVerticalRotation = maxVerticalRotation;
    }
    
    /**
     * returns the minimal vertical rotation angle of the camera around the target
     * @return
     */
    public float getMinVerticalRotation() 
    {
        return minVerticalRotation;
    }
    
    /**
     * sets the minimal vertical rotation angle of the camera around the target default is 0;
     * @param minHeight
     */
    public void setMinVerticalRotation(float minHeight) 
    {
        this.minVerticalRotation = minHeight;
    }
    
    /**
     * Sets the default distance at start of applicaiton
     * @param defaultDistance
     */
    public void setDefaultDistance(float defaultDistance) 
    {
        camDistance = defaultDistance;
    }
    
    /**
     * sets the default horizontal rotation of the camera at start of the application
     * @param angle
     */
    public void setDefaultHorizontalRotation(float angle) 
    {
        camRotation = angle;
    }
    
    /**
     * sets the default vertical rotation of the camera at start of the application
     * @param angle
     */
    public void setDefaultVerticalRotation(float angle) {
        camVRotation = angle;
    }
}
