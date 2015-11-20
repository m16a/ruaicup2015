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

		for (int i=0; i<ITERATION_COUNT; i++)
			updatePos(cp); 
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
}
