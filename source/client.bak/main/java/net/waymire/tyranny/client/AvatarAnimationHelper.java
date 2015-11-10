package net.waymire.tyranny.client;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.objects.PhysicsCharacter;

public class AvatarAnimationHelper 
{
	private final AnimChannel animChannel;
	private final AnimChannel attackChannel;
	private final PhysicsCharacter physicBody;
	
	public AvatarAnimationHelper(PhysicsCharacter physicBody, AnimChannel animChannel,AnimChannel attackChannel)
	{
		this.animChannel = animChannel;
		this.attackChannel = attackChannel;
		this.physicBody = physicBody;
	}
	
	protected void idle()
	{
		animChannel.setAnim("stand");
		animChannel.setSpeed(0.5f);
	}
	
	protected boolean forward(boolean pressed)
	{
		if (pressed)
		{
			animChannel.setAnim("Walk");
			animChannel.setSpeed(AvatarConstants.MOVE_WALK_SPEED * 2f);
			animChannel.setLoopMode(LoopMode.Loop);
			return true;
		}
		else
		{
			idle();
			return false;
		}
	}
	
	protected boolean backward(boolean pressed)
	{
		if (pressed)
		{
			animChannel.setAnim("Walk");
			animChannel.setSpeed(AvatarConstants.MOVE_BACKWARD_SPEED * 2f);
			animChannel.setLoopMode(LoopMode.Loop);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected boolean rightward(boolean pressed)
	{
		if (pressed)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected boolean leftward(boolean pressed)
	{
		if (pressed)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected boolean jump(boolean pressed)
	{
		if (pressed)
		{
			this.physicBody.jump();
			return true;
			/*
			if (this.physicBody.onGround())
			{
				animChannel.setAnim("JumpStart");
				animChannel.setSpeed(AvatarConstants.MOVE_WALK_SPEED / 1.8f);
				animChannel.setLoopMode(LoopMode.DontLoop);
				this.physicBody.jump();
				return true;
			}
			return false;
			*/
		}
		else
		{
			return false;
		}
	}
	
	protected boolean attack(boolean pressed)
	{
		if(pressed)
		{
			attackChannel.setAnim("Dodge", 01.f);
			attackChannel.setLoopMode(LoopMode.DontLoop);
			return true;
		}
		else
		{
			return false;
		}
	}
}
