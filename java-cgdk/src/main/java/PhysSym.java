import model.Car;
import model.Game;
import model.Move;
import model.World;


public final class PhysSym
{
	public static final int ITERATION_COUNT = 10;
	public static final double EPSILON = 1.0E-7D;   
	protected static double updateFactor = 1.0D / ITERATION_COUNT ;// / 60;

  //static double ANGULAR_FACTOR = 0.001745;
  static double POWER_PER_TICK = 0.025;

	public static void step(CarProxy cp, Game game)
	{
		//convert input power to force
		
		cp.m_power += Math.max(Math.min(cp.m_in_power - cp.m_power, POWER_PER_TICK), -POWER_PER_TICK);
   
 
    double c = Math.cos(cp.m_angle);
    double s = Math.sin(cp.m_angle);
	
		double p = cp.m_power * (cp.m_power >=0 ?  game.getBuggyEngineForwardPower() : game.getBuggyEngineRearPower());
		if (cp.m_in_brake != true)
			cp.m_f = new Vector2D(c * p, s * p);


		cp.m_wheel += Math.max(Math.min(cp.m_in_wheel - cp.m_wheel, game.getCarWheelTurnChangePerTick()), -game.getCarWheelTurnChangePerTick());
    cp.m_w -= cp.m_medianW;
    Vector2D dirVector = new Vector2D(1.0, 0.0).rotate(cp.m_angle);
    double angSpeedPart = cp.m_v.dotProduct(dirVector);
    cp.m_medianW = cp.m_wheel * game.getCarAngularSpeedFactor() * angSpeedPart;
    cp.m_w += cp.m_medianW;


		for (int i=0; i<ITERATION_COUNT; i++)
		{
			updatePos(cp); 
			updateAngle(cp, game);
		}	
	}
   
	public static void updatePos(CarProxy cp) 
	{
		if (cp.m_v.length() > 0.0D)
			//  pos = pos + vel * update     
			cp.m_pos = cp.m_pos.add(cp.m_v.scale(updateFactor)); 

		if (cp.m_f.length() > 0.0D)
			cp.m_v = cp.m_v.add(cp.m_f.scale(cp.m_Minv).scale(updateFactor));

		if (cp.m_airFrict >= 1.0D)
			cp.m_v = cp.m_medianV;
		else if (cp.m_airFrict > 0.0D)
		{
			cp.applyMovementAirFriction(updateFactor);
			if (cp.m_medianV.sub(cp.m_v).length() < EPSILON)
				cp.m_v = cp.m_medianV;
		}
		cp.m_v = cp.m_v.sub(cp.m_medianV);
		cp.applyFriction(updateFactor);
		cp.m_v = cp.m_v.add(cp.m_medianV);
				
	}

	public static void updateAngle(CarProxy cp, Game game)
	{
		cp.m_angle += cp.m_w * updateFactor;  

		if (cp.m_rotAirFrict >= 1.0D)
			cp.m_w = cp.m_medianW; 
		else if (cp.m_rotAirFrict > 0.0D)
		{
			cp.applyRotationAirFriction(updateFactor);
			if (Math.abs(cp.m_w - cp.m_medianW) < EPSILON)
				cp.m_w = cp.m_medianW;			
		}

		double angularVelocity = cp.m_w - cp.m_medianW;

		if (Math.abs(angularVelocity) > 0.0D)
		{
			double rotationFrictionFactor = cp.m_rotFrictF * updateFactor;

			if (rotationFrictionFactor >= Math.abs(angularVelocity))
				cp.m_w = cp.m_medianW;
			else if (rotationFrictionFactor > 0.0D)
			{
				if (angularVelocity > 0.0D)
					cp.m_w = angularVelocity - cp.m_rotFrictF + cp.m_medianW;
				else
					cp.m_w = angularVelocity + cp.m_rotFrictF + cp.m_medianW;
			}
		}
	}

}
