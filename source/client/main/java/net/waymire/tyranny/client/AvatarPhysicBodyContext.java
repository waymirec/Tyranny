package net.waymire.tyranny.client;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.math.Vector3f;

public class AvatarPhysicBodyContext extends AbstractPhysicBodyContext 
{
	private final PhysicsCharacter physicBody;
	
	public AvatarPhysicBodyContext()
	{
		this.physicBody = new PhysicsCharacter(new SphereCollisionShape(AvatarConstants.COLLISION_SHAPE_RADIUS), .01f);
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app)
	{
		assert(getBulletAppState() != null);
		
		this.physicBody.setJumpSpeed(32);
		this.physicBody.setFallSpeed(32);
		this.physicBody.setGravity(32);
		this.physicBody.setPhysicsLocation(new Vector3f(0, 1000, 0));
		getBulletAppState().getPhysicsSpace().add(this.physicBody);
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
