import model.Car;
import model.Game;
import model.Move;
import model.World;
import static java.lang.Math.pow;

public final class CarProxy
{
	public Vector2D m_v;
	public Vector2D m_pos;
	public Vector2D m_f;
	public double m_Minv;
	public double m_airFrict;
	public double m_rotAirFrict;

	public double m_longFrict;
	public double m_crossFrict; 
	public double m_rotFrictF;
	public Vector2D m_medianV;//zero

	public double m_medianW;

	public double m_power;
	public double m_wheel;
	
	//input parameters
	public double m_in_power;
	public boolean m_in_brake;
	public double m_in_wheel;

	public double m_angle;
	public double m_w;

  private double lastMovementAirFrictionFactor;                                                      
	private double lastMovementUpdateFactor;                                                            
	private Double lastMovementTransferFactor;  

	private Double  lastRotationTransferFactor; 
	private double lastRotationAirFrictionFactor;
	private double lastRotationUpdateFactor;
	public Game m_game;

	public CarProxy(Car c, Game game)
	{
		this.m_v = new Vector2D(c.getSpeedX(), c.getSpeedY());
		this.m_pos = new Vector2D(c.getX(), c.getY());
		this.m_f = new Vector2D(0,0);
		this.m_Minv = 1.0D / c.getMass();
		this.m_medianV = new Vector2D(0,0);
		this.m_medianW = c.getAngularSpeed();
		this.m_angle = c.getAngle();
		this.m_w = c.getAngularSpeed();
		this.m_power = c.getEnginePower();
		this.m_wheel = c.getWheelTurn();		

		this.m_longFrict = game.getCarLengthwiseMovementFrictionFactor();
		this.m_crossFrict = game.getCarCrosswiseMovementFrictionFactor();
		this.m_airFrict = game.getCarMovementAirFrictionFactor(); 
		this.m_rotAirFrict = game.getCarRotationAirFrictionFactor();
		this.m_rotFrictF = game.getCarRotationFrictionFactor();
		this.m_game = game;
		this.m_in_brake = false; 	
	}

   public void applyMovementAirFriction(double updateFactor)
	 {                                                      
			if (lastMovementTransferFactor == null                                                          
				|| m_airFrict != lastMovementAirFrictionFactor                                 
				|| updateFactor != lastMovementUpdateFactor)
			{                                                
				lastMovementAirFrictionFactor = m_airFrict;                                   
				lastMovementUpdateFactor = updateFactor;                                                      
				lastMovementTransferFactor = pow(1.0D - m_airFrict, updateFactor);
			}

			m_v = m_v.sub(m_medianV).scale(lastMovementTransferFactor).add(m_medianV);
	}
	
	public void applyFriction(double updateFactor)
	{
		double velocityLength = m_v.length();                                     
		if (velocityLength <= 0.0D)
			return;                                                                         
		
		double dlf = (m_in_brake ? m_crossFrict : m_longFrict) * updateFactor;                    
		double dcf = m_crossFrict * updateFactor;                      
		Vector2D forward = new Vector2D(1.0D, 0.0D).rotate(m_angle);
		Vector2D crosswise = new Vector2D(0.0D, 1.0D).rotate(m_angle);                    
		
		double vel_forw = m_v.dotProduct(forward);                  
																																															
		if ( vel_forw >= 0.0D) {                                                                 
			vel_forw -= dlf;                                               
			if (vel_forw < 0.0D)                                                              
				vel_forw = 0.0D;                                                                
		} else {
			vel_forw += dlf;                                               
				if (vel_forw > 0.0D)                                                               
						vel_forw = 0.0D;                                                                
		}                                                                                           
		
		double vel_cross = m_v.dotProduct(crosswise);                    
																																																
		if (vel_cross >= 0.0D) 
		{                                                                  
			vel_cross -= dcf;                                                 
			if (vel_cross < 0.0D)                                                               
					vel_cross = 0.0D;                                                                 
		} else {                                                                                  
			vel_cross += dcf;                                                 
			if (vel_cross > 0.0D)                                                               
				vel_cross = 0.0D;                                                                 
		}                                                                                         
		
		m_v = forward.scale(vel_forw).add(crosswise.scale(vel_cross));
	}  

	public void applyRotationAirFriction(double updateFactor)
	{
		if (lastRotationTransferFactor == null
			|| m_rotAirFrict != lastRotationAirFrictionFactor
      || updateFactor != lastRotationUpdateFactor)
		{
      lastRotationAirFrictionFactor = m_rotAirFrict;
      lastRotationUpdateFactor = updateFactor;
      lastRotationTransferFactor = pow(1.0D - m_rotAirFrict, updateFactor);
    }

		m_w = (m_w - m_medianW) * lastRotationTransferFactor + m_medianW;
  }

}


