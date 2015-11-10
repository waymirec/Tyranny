package net.waymire.tyranny.client;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.scene.Spatial;

public class PlayerInputActionListener extends AbstractAppState implements ActionListener 
{
	private final PhysicsCharacter physicBody;
	private final AvatarAnimationEventListener avatarAnimEventListener;
	
	private boolean leftward = false;
	private boolean rightward = false;
	private boolean forward = false;
	private boolean backward = false;
	private boolean jump = false;
	private boolean strafe = false;
	private boolean attack = false;
	
	public PlayerInputActionListener(PhysicsCharacter physicBody, Spatial avatar)
	{
		this.physicBody = physicBody;
		this.avatarAnimEventListener = new AvatarAnimationEventListener(this, this.physicBody, avatar);
	}
	
	public void initialize(AppStateManager stateManager, Application app)
	{
		stateManager.attach(this.avatarAnimEventListener);
		
		app.getInputManager().addMapping(PlayerInputConstants.MOVE_LEFT, new KeyTrigger(KeyInput.KEY_A));
		app.getInputManager().addMapping(PlayerInputConstants.MOVE_RIGHT, new KeyTrigger(KeyInput.KEY_D));
		app.getInputManager().addMapping(PlayerInputConstants.MOVE_FORWARD, new KeyTrigger(KeyInput.KEY_W));
		app.getInputManager().addMapping(PlayerInputConstants.MOVE_BACKWARD, new KeyTrigger(KeyInput.KEY_X));
		app.getInputManager().addMapping(PlayerInputConstants.MOVE_JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
		app.getInputManager().addMapping(PlayerInputConstants.MOVE_RUN, new KeyTrigger(KeyInput.KEY_LSHIFT));
		app.getInputManager().addMapping(PlayerInputConstants.MOVE_STRAFE, new KeyTrigger(KeyInput.KEY_LCONTROL));
		
		app.getInputManager().addMapping(PlayerInputConstants.ACTION_SHOOT, new KeyTrigger(KeyInput.KEY_RETURN));
		app.getInputManager().addMapping(PlayerInputConstants.MOUSE_LOOK_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		app.getInputManager().addMapping(PlayerInputConstants.MOUSE_LOOK_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		
		app.getInputManager().addListener(this, PlayerInputConstants.MOVE_LEFT);
		app.getInputManager().addListener(this, PlayerInputConstants.MOVE_RIGHT);
		app.getInputManager().addListener(this, PlayerInputConstants.MOVE_FORWARD);
		app.getInputManager().addListener(this, PlayerInputConstants.MOVE_BACKWARD);
		app.getInputManager().addListener(this, PlayerInputConstants.MOVE_JUMP);
		app.getInputManager().addListener(this, PlayerInputConstants.MOVE_RUN);
		app.getInputManager().addListener(this, PlayerInputConstants.MOVE_STRAFE);
		app.getInputManager().addListener(this, PlayerInputConstants.ACTION_SHOOT);
		app.getInputManager().addListener(this,  PlayerInputConstants.MOUSE_LOOK_DOWN);
		app.getInputManager().addListener(this,  PlayerInputConstants.MOUSE_LOOK_UP);
	}
	
	@Override
	public void onAction(String binding, boolean keyPressed, float tpf) 
	{
		switch(binding)
		{
			case PlayerInputConstants.MOVE_STRAFE:
			{
				this.strafe = keyPressed;
			}
			case PlayerInputConstants.MOVE_LEFT:
			{
				this.leftward = this.avatarAnimEventListener.getAnimHelper().leftward(keyPressed);
				break;
			}
			case PlayerInputConstants.MOVE_RIGHT:
			{
				this.rightward = this.avatarAnimEventListener.getAnimHelper().rightward(keyPressed);
				break;
			}
			case PlayerInputConstants.MOVE_FORWARD:
			{
				this.forward = this.avatarAnimEventListener.getAnimHelper().forward(keyPressed);
				break;
			}
			case PlayerInputConstants.MOVE_BACKWARD:
			{
				this.backward = this.avatarAnimEventListener.getAnimHelper().backward(keyPressed);
				break;
			}
			case PlayerInputConstants.MOVE_JUMP:
			{
				this.jump = this.avatarAnimEventListener.getAnimHelper().jump(keyPressed);
				break;
			}
			case PlayerInputConstants.ACTION_SHOOT:
			{
				this.attack = this.avatarAnimEventListener.getAnimHelper().attack(keyPressed);
			}
		}
	}

	public boolean isStrafing()
	{
		return strafe;
	}

	public boolean isLeftward()
    {
        return this.leftward;
    }
	
	public boolean isRightward()
    {
        return this.rightward;
    }
	
	public boolean isForward()
    {
        return this.forward;
    }
	
	public boolean isBackward()
    {
        return this.backward;
    }
	
	public boolean isJump()
    {
        return this.jump;
    }
	
	public boolean isAttacking()
	{
		return this.attack;
	}
}
