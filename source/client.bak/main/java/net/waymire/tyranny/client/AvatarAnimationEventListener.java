package net.waymire.tyranny.client;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.scene.Spatial;

public class AvatarAnimationEventListener extends AbstractAppState implements AnimEventListener 
{
	private final AnimChannel animationChannel;
	private final AnimChannel attackChannel;
	private final AnimControl control;
	@SuppressWarnings("unused")
	private final PlayerInputActionListener playerInputActionListener;
	private final AvatarAnimationHelper animHelper;
	private final PhysicsCharacter physicBody;
	
	public AvatarAnimationEventListener(PlayerInputActionListener playerInputActionListener, PhysicsCharacter physicBody, Spatial avatarMesh)
	{
		this.playerInputActionListener = playerInputActionListener;
		this.control = avatarMesh.getControl(AnimControl.class);
		assert(this.control != null);
		this.animationChannel = this.control.createChannel();
		
		this.attackChannel = this.control.createChannel();
		this.attackChannel.addBone(control.getSkeleton().getBone("uparm.right"));
		this.attackChannel.addBone(control.getSkeleton().getBone("arm.right"));
		this.attackChannel.addBone(control.getSkeleton().getBone("hand.right"));
		
		
		this.physicBody = physicBody;
		this.animHelper = new AvatarAnimationHelper(this.physicBody, this.animationChannel, this.attackChannel);
	}
	
	public void initialize(AppStateManager stateManager, Application app)
	{
		this.control.addListener(this);
		this.animationChannel.setAnim("stand");
		this.animationChannel.setSpeed(0.5f);
	}
	
	@Override
	public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) 
	{
		
	}

	@Override
	public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) 
	{

	}

	protected AnimChannel getAnimationChannel()
	{
		return animationChannel;
	}

	protected AnimChannel getAttackChannel()
	{
		return attackChannel;
	}
	
	protected AnimControl getControl()
	{
		return control;
	}
	
	protected AvatarAnimationHelper getAnimHelper()
    {
        return animHelper;
    }
}
