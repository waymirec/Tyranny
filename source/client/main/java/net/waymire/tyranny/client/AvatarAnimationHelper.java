package net.waymire.tyranny.client;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.objects.PhysicsCharacter;

public class AvatarAnimationHelper 
{
	private final AnimChannel baseAnimChannel;
	private final AnimChannel topAnimChannel;
	private final PhysicsCharacter physicBody;
	
	public AvatarAnimationHelper(PhysicsCharacter physicBody, AnimChannel baseAnimChannel, AnimChannel topAnimChannel)
	{
		this.baseAnimChannel = baseAnimChannel;
		this.topAnimChannel = topAnimChannel;
		this.physicBody = physicBody;
	}
	
	protected void idle()
	{
		baseAnimChannel.setAnim("IdleBase");
		topAnimChannel.setAnim("IdleTop");
	}
	
	protected boolean forward(boolean pressed)
	{
		if (pressed)
		{
			baseAnimChannel.setAnim("RunBase");
			baseAnimChannel.setSpeed(AvatarConstants.MOVE_WALK_SPEED * 20f);
			baseAnimChannel.setLoopMode(LoopMode.Loop);
			
			topAnimChannel.setAnim("RunTop");
			topAnimChannel.setSpeed(AvatarConstants.MOVE_WALK_SPEED * 20f);
			topAnimChannel.setLoopMode(LoopMode.Loop);
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
			baseAnimChannel.setAnim("RunBase");
			baseAnimChannel.setSpeed(AvatarConstants.MOVE_BACKWARD_SPEED * 20f);
			baseAnimChannel.setLoopMode(LoopMode.Loop);
			
			topAnimChannel.setAnim("RunTop");
			topAnimChannel.setSpeed(AvatarConstants.MOVE_BACKWARD_SPEED * 20f);
			topAnimChannel.setLoopMode(LoopMode.Loop);
			
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
			/*
			if (this.physicBody.onGround())
			{
				animChannel.setAnim("HighJump");
				animChannel.setSpeed(AvatarConstants.FORWARD_MOVE_SPEED / 1.8f);
				animChannel.setLoopMode(LoopMode.DontLoop);
				this.physicBody.jump();
			}
			*/
			return true;
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
			topAnimChannel.setAnim("DrawSwords");
			topAnimChannel.setLoopMode(LoopMode.DontLoop);
			
			topAnimChannel.setAnim("SliceVertical");
			topAnimChannel.setLoopMode(LoopMode.DontLoop);
			return true;
		}
		else
		{
			idle();
			return false;
		}
	}
}
