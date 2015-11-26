import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.*;
import java.awt.Color;

public final class MyStrategy implements Strategy {
    
	int tickN = 0;
	public static int WIDTH = 800;

	public static Vector2D getTile(double x, double y)
	{
		return new Vector2D((int)x / WIDTH, (int)y / WIDTH);
	}

  @Override
	public void move(Car self, World world, Game game, Move move) 
	{

		if (Global.s_vc == null)
			Global.s_vc = new VisualClient();

			if (Global.s_wave == null)
				Global.s_wave  = new Wave(world);
	
		//move.setThrowProjectile(true);
		//move.setSpillOil(true);

		// if (world.getTick() > game.getInitialFreezeDurationTicks()) {
		//     move.setUseNitro(true);
		// }
		double v_x = self.getSpeedX();
		double v_y = self.getSpeedY();
		long ms0 = System.currentTimeMillis();
		
		CarProxy cp = new CarProxy(self, game);
		//s_vc.beginPost();

		//s_vc.fillCircle(0,0,5000,Color.black);		
		//s_vc.endPost();

		//if (tickN < 300)
		//{		
			move.setEnginePower(1.0D);
			cp.m_in_power = 1.0D;
		//}else{
			//move.setBrake(true);
			//cp.m_in_brake = true;
			//move.setWheelTurn(1);
			//cp.m_in_wheel = 1;		
		//}

		Vector2D input = new Vector2D(0,0);

		if (tickN > 170)
			input = TrajBuilder.findBestTrajectory(cp, game);
		
		if ((int)input.x() == 0)
			move.setWheelTurn(1 * input.y());
		//PhysSym.step(cp, game);

		long ms1 = System.currentTimeMillis();
		//if (tickN > 290 && tickN < 310)
		{
			System.out.printf("time %d ms\n", ms1 - ms0);
			System.out.printf("tickN:%d curr vel (%.5f, %.5f), next vel %s\n", tickN, v_x, v_y,  cp.m_v.toString());
			//System.out.printf("curr power %.5f, next power %.5f\n", self.getEnginePower(), cp.m_power);
			System.out.printf("cur turn %.5f, next turn %.5f\n", self.getWheelTurn(), cp.m_wheel);
			System.out.printf("w %.5f, next w %.5f\n", self.getAngularSpeed(), cp.m_w);
			System.out.printf("angle %.5f, next angle %.5f\n", self.getAngle(), cp.m_angle);


			Vector<Vector2D> vs = Global.s_wave.find(world.getWaypoints()[0][0],world.getWaypoints()[0][1],world.getWaypoints()[1][0],world.getWaypoints()[1][1]);

			//Global.s_vc.beginPost();
			for (Vector2D vec : vs)
			{
				//System.out.printf("%s\n", vec);
				//Global.s_vc.fillCircle((int)vec.x() * 800 + 400, (int)vec.y() * 800 + 400, 50, Color.red);
			}				
			//Global.s_vc.endPost();
		}
		tickN = tickN + 1;
	}
}
