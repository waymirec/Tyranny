package net.waymire.tyranny.client;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class AvatarConstants 
{
	public static final Quaternion YAW090 = new Quaternion().fromAngleAxis(FastMath.PI/2,new Vector3f(0,1,0));
    public static final Quaternion ROT_LEFT = new Quaternion().fromAngleAxis(FastMath.PI/80,new Vector3f(0,1,0));
    public static final Quaternion ROT_RIGHT = new Quaternion().fromAngleAxis(-FastMath.PI/80,new Vector3f(0,1,0));
    
	public static final float COLLISION_SHAPE_CENTRAL_POINT = 0.0f;
	public static final float COLLISION_SHAPE_RADIUS = 6.0f;
	
	public static final float PHYSIC_BODY_MASS = 1.0f;
	
	public static final float MOVE_ROT_SPEED = 5.0f;
	public static final float MOVE_WALK_SPEED = 0.05f;
    public static final float MOVE_RUN_SPEED = 0.10f;
    public static final float MOVE_BACKWARD_SPEED = 0.05f;
    public static final float MOVE_STRAFE_SPEED = 0.8f;
}
