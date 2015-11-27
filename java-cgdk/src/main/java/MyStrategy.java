import model.Car;
import model.Game;
import model.Move;
import model.World;

import java.util.*;
import java.awt.Color;

public final class MyStrategy implements Strategy {
    
	int tickN = 0;
	public static int WIDTH = 800;

	public static Vector2D turn_input = new Vector2D();
	public static Vector2D brake_input = new Vector2D();
	public static int turn_side = 0;
	public static boolean inputReady = false;
	public static Vector2D getTile(double x, double y)
	{
		return new Vector2D((int)x / WIDTH, (int)y / WIDTH);
	}


	public static Vector<Vector2D> entirePath = new Vector<Vector2D>();

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

		Global.s_vc.beginPost();
	
	
		if (tickN > 170 && (tickN % 10) == 0)
		{		
				Vector2D[] input = TrajBuilder.findBestTrajectory(cp, game);
				
				turn_input = input[0].add(new Vector2D(tickN, tickN));
				turn_side = (int)(input[1].x());
				brake_input = input[2].add(new Vector2D(tickN, tickN));			
				inputReady = true;
		}	
		
		if (inputReady)
		{
			int i = tickN;
				int tC = (int)((turn_input.x() + turn_input.y()) / 2);//middle tick
				if (i > turn_input.x() && i < tC)
					move.setWheelTurn(1*turn_side);
				else if (i >= tC && i < turn_input.y())
					move.setWheelTurn(-1*turn_side);
				else if (i < turn_input.x() || i > turn_input.y())
					cp.m_in_wheel = 0;

				if (i > brake_input.x() && i < brake_input.y())
					move.setBrake(true);
				else
					move.setBrake(false);

		}

/*	
		if ((int)input[0].x() == 0)
			move.setWheelTurn(1 * input[1].x());
		
		if ((int)input[2].x() == 0)
			move.setBrake(true);
		else 
			move.setBrake(false);
	*/	
		Global.s_vc.fillCircle(self.getX() + 200, self.getY() - 100, 25, move.isBrake() ? Color.red :Color.black);		


		Color c = Math.abs(move.getWheelTurn()) < 0.0000001 ? Color.black : (move.getWheelTurn() > 0 ? Color.green : Color.blue);

		Global.s_vc.fillCircle(self.getX() + 150, self.getY() - 100, 25, c);		
		Global.s_vc.endPost();


		long ms1 = System.currentTimeMillis();
		//if (tickN > 290 && tickN < 310)
		{
			System.out.printf("time %d ms\n", ms1 - ms0);
			System.out.printf("tickN:%d curr vel (%.5f, %.5f), next vel %s\n", tickN, v_x, v_y,  cp.m_v.toString());
			//System.out.printf("curr power %.5f, next power %.5f\n", self.getEnginePower(), cp.m_power);
			System.out.printf("cur turn %.5f, next turn %.5f\n", self.getWheelTurn(), cp.m_wheel);
			System.out.printf("w %.5f, next w %.5f\n", self.getAngularSpeed(), cp.m_w);
			System.out.printf("angle %.5f, next angle %.5f\n", self.getAngle(), cp.m_angle);



			//draw waypoints
			Global.s_vc.beginPost();
			int[][] wpnts = world.getWaypoints();

			entirePath.clear();
			for (int i = 0; i < wpnts.length-1; i++)
			{
				
				Vector<Vector2D> vs = Global.s_wave.find(wpnts[i][0],wpnts[i][1], wpnts[i+1][0],wpnts[i+1][1]);
				Global.s_vc.text((int)wpnts[i][0] * 800 + 400, (int)wpnts[i][1]  * 800 + 400, Integer.toString(i), Color.red);
			
				//pop init point
				vs.remove(0);

				entirePath.addAll(vs);
				
			}
			Global.s_vc.endPost();
			

			Global.s_vc.beginPost();
			for (Vector2D vec : entirePath)
			{
				System.out.printf("%s\n", vec);
				Global.s_vc.fillCircle((int)vec.x() * 800 + 400, (int)vec.y() * 800 + 400, 50, Color.red);
			}				
Global.s_vc.endPost();
		}
		tickN = tickN + 1;
	}
}
