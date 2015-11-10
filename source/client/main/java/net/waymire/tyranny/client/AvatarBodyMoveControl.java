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
		Vector3f viewDir = spatial.getControl(BetterCharacterControl.class).getViewDirection().clone().normalize();
		
		if(physicBody.onGround())
		{
			walkDirection.set(Vector3f.ZERO);
		}
		//walkDirection.set(0, 0, 0); //critical

		if(playerInputActionListener.isLeftward())
		{
			AvatarConstants.ROT_LEFT.multLocal(viewDir);
		}
		if(playerInputActionListener.isRightward())
		{
			AvatarConstants.ROT_RIGHT.multLocal(viewDir);
		}
		if(playerInputActionListener.isForward())
		{
			if (physicBody.onGround())
			{
				walkDirection.addLocal(viewDir);
				float speed = playerInputActionListener.isRunning() ? AvatarConstants.MOVE_RUN_SPEED : AvatarConstants.MOVE_WALK_SPEED;
				walkDirection.multLocal(speed);
			}
		}
		if(playerInputActionListener.isBackward())
		{
			if(physicBody.onGround())
			{
				walkDirection.addLocal(viewDir.negate());
				walkDirection.multLocal(AvatarConstants.MOVE_BACKWARD_SPEED);	
			}
		}
		//viewDir.multLocal(tpf);
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
