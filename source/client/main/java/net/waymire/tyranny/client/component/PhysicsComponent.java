package net.waymire.tyranny.client.component;

import net.waymire.tyranny.client.AvatarConstants;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class PhysicsComponent extends AbstractAppState implements GameComponent 
{
	private static final BulletAppState bulletAppState = new BulletAppState();
	
	private final PhysicsCharacter physicBody;
	private final BetterCharacterControl characterControl;
	private final Node avatar;
	
	public PhysicsComponent(Node avatar)
	{
		this.physicBody = new PhysicsCharacter(new SphereCollisionShape(AvatarConstants.COLLISION_SHAPE_RADIUS), .01f);
		this.characterControl = new BetterCharacterControl(AvatarConstants.COLLISION_SHAPE_RADIUS, AvatarConstants.COLLISION_SHAPE_RADIUS * 2, AvatarConstants.PHYSIC_BODY_MASS);
		this.avatar = avatar;
		this.avatar.addControl(characterControl);
	}

	public void attachBulletAppState(AppStateManager stateManager)
	{
		stateManager.attach(bulletAppState);
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		assert(bulletAppState != null);
		
		this.physicBody.setJumpSpeed(32);
		this.physicBody.setFallSpeed(32);
		this.physicBody.setGravity(64);
		this.physicBody.setPhysicsLocation(new Vector3f(0, 0, 0));
		
		bulletAppState.getPhysicsSpace().add(physicBody);
	}
	
	@Override
    public void update(float tpf)
    {
    }
 
    @Override
    public void cleanup()
    {
        super.cleanup();
    }
    
    public PhysicsCharacter getPhysicBody()
    {
        return this.physicBody;
    }    
}
