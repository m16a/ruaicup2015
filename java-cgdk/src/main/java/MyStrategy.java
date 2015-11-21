import model.Car;
import model.Game;
import model.Move;
import model.World;


public final class MyStrategy implements Strategy {
    
	int tickN = 0;
  @Override
	public void move(Car self, World world, Game game, Move move) 
	{
		//move.setThrowProjectile(true);
		//move.setSpillOil(true);

		// if (world.getTick() > game.getInitialFreezeDurationTicks()) {
		//     move.setUseNitro(true);
		// }
		double v_x = self.getSpeedX();
		double v_y = self.getSpeedY();
		long ms0 = System.currentTimeMillis();
		
		CarProxy cp = new CarProxy(self, game);

		if (tickN < 300)
		{		
			move.setEnginePower(0.5D);
			cp.m_in_power = 0.5D;
		}else{
			move.setBrake(true);
			cp.m_in_brake = true;
			move.setWheelTurn(1);
			cp.m_in_wheel = 1;		
		}
		PhysSym.step(cp, game);

		long ms1 = System.currentTimeMillis();
		if (tickN > 290 && tickN < 310)
		{
			System.out.printf("time %d ms\n", ms1 - ms0);
			System.out.printf("tickN:%d curr vel (%.5f, %.5f), next vel %s\n", tickN, v_x, v_y,  cp.m_v.toString());
			//System.out.printf("curr power %.5f, next power %.5f\n", self.getEnginePower(), cp.m_power);
			System.out.printf("cur turn %.5f, next turn %.5f\n", self.getWheelTurn(), cp.m_wheel);
			System.out.printf("w %.5f, next w %.5f\n", self.getAngularSpeed(), cp.m_w);
			System.out.printf("angle %.5f, next angle %.5f\n", self.getAngle(), cp.m_angle);
		}
		tickN = tickN + 1;
	}
}
