import model.Car;
import model.Game;
import model.Move;
import model.World;


public final class MyStrategy implements Strategy {
    
	int tickN = 0;
  @Override
	public void move(Car self, World world, Game game, Move move) 
	{
		move.setEnginePower(0.5D);
		//move.setThrowProjectile(true);
		//move.setSpillOil(true);

		// if (world.getTick() > game.getInitialFreezeDurationTicks()) {
		//     move.setUseNitro(true);
		// }
		double v_x = self.getSpeedX();
		double v_y = self.getSpeedY();
		long ms0 = System.currentTimeMillis();
		
		CarProxy cp = new CarProxy(self, game);
		cp.m_in_power = 0.5D;
	
		PhysSym.step(cp, game);

		long ms1 = System.currentTimeMillis();
		if (tickN > 178 && tickN < 210)
		{
			System.out.printf("time %d ms\n", ms1 - ms0);
			System.out.printf("tickN:%d curr vel (%.5f, %.5f), next vel %s\n", tickN, v_x, v_y,  cp.m_v.toString());
			System.out.printf("curr power %.5f, next power %.5f\n", self.getEnginePower(), cp.m_power);
		}
		tickN = tickN + 1;
	}
}
