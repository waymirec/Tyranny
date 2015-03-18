package net.waymire.tyranny.client;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class AvatarBodyMoveControl extends AbstractControl 
{
	private final Vector3f walkDirection = new Vector3f();
	private final Camera cam;
	private final PhysicsCharacter physicBody;
	private final PlayerInputActionListener playerInputActionListener;
	
	public AvatarBodyMoveControl(PlayerInputActionListener playerInputActionListener, PhysicsCharacter physicBody, Camera cam)
	{
		this.playerInputActionListener = playerInputActionListener;
		this.physicBody = physicBody;
		this.cam = cam;
	}
	
	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) 
	{

	}

	@Override
	protected void controlUpdate(float tpf) 
	{
		correctDirectionVectors(tpf);
	}

	public void correctDirectionVectors(float tpf)
	{
		Vector3f viewDir = spatial.getControl(BetterCharacterControl.class).getViewDirection().normalize();
		Vector3f leftDir = AvatarConstants.YAW090.mult(viewDir).normalize();
		viewDir.y = 0;
		
		//Affect forward, backward move speed 0.6f lower - 1.0f faster
		//Vector3f camDirVector = cam.getDirection().clone().multLocal(AvatarConstants.FORWARD_MOVE_SPEED);
		
		//Affect left, right move speed 0.6f lower - 1.0f faster
		//Vector3f camLeftVector = cam.getLeft().clone().multLocal(AvatarConstants.SIDEWARD_MOVE_SPEED);
		
		walkDirection.set(0, 0, 0); //critical

		if(playerInputActionListener.isLeftward())
		{
			if(playerInputActionListener.isStrafing())
			{
				//walkDirection.addLocal(leftDir.mult(0.2f));
				//walkDirection.multLocal(AvatarConstants.MOVE_STRAFE_SPEED);
			}
			else
			{
				AvatarConstants.ROT_LEFT.multLocal(viewDir);
			}
		}
		if(playerInputActionListener.isRightward())
		{
			if(playerInputActionListener.isStrafing())
			{
				//walkDirection.addLocal(leftDir.negate().mult(0.2f));
				//walkDirection.multLocal(AvatarConstants.MOVE_STRAFE_SPEED);
			}
			else
			{
				AvatarConstants.ROT_RIGHT.multLocal(viewDir);
			}
		}
		if(playerInputActionListener.isForward())
		{
			walkDirection.addLocal(viewDir.mult(0.2f));
			walkDirection.multLocal(AvatarConstants.MOVE_WALK_SPEED);
		}
		if(playerInputActionListener.isBackward())
		{
			walkDirection.addLocal(viewDir.negate().mult(0.1f));
			walkDirection.multLocal(AvatarConstants.MOVE_BACKWARD_SPEED);
		}
		viewDir.multLocal(tpf);
		spatial.getControl(BetterCharacterControl.class).setViewDirection(viewDir);

		physicBody.setWalkDirection(walkDirection); //Critical
		
		//Avoid vibration
		spatial.setLocalTranslation(physicBody.getPhysicsLocation());
		//Translate Node accordingly
		spatial.getControl(BetterCharacterControl.class).warp(physicBody.getPhysicsLocation());
		//Rotate Node accordingly to camera
		//spatial.getControl(BetterCharacterControl.class).setViewDirection(cam.getDirection().negate());
	}
}
