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
	
	public static int waypointsPassed = 0;
	public static int nextX = -1;
	public static int nextY = -1;

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
	
		if (nextX == -1 || (nextX !=self.getNextWaypointX() || nextY != self.getNextWaypointY()))
		{
			nextX = self.getNextWaypointX();
			nextY = self.getNextWaypointY();
			waypointsPassed = (waypointsPassed + 1) % world.getWaypoints().length;
		}


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
			double in_p = 0.5D;	
			move.setEnginePower(in_p);
			cp.m_in_power = in_p;
		//}else{
			//move.setBrake(true);
			//cp.m_in_brake = true;
			//move.setWheelTurn(1);
			//cp.m_in_wheel = 1;		
		//}

	
	
		if (tickN > 170 && (tickN % 10) == 0)
		{		
				Vector2D[] input = TrajBuilder.findBestTrajectory(cp, game);
				
				turn_input = input[0].add(new Vector2D(tickN, tickN));
				turn_side = (int)(input[1].x());
				brake_input = input[2].add(new Vector2D(tickN, tickN));			
				inputReady = true;
		}	
		
		if (inputReady)
			fillMoveFromInputs(move, tickN, turn_input, brake_input, turn_side);

		Global.s_vc.beginPost();
	Global.s_vc.fillCircle(self.getX() + 200, self.getY() - 100, 25, move.isBrake() ? Color.red :Color.black);		


		Color c = Math.abs(move.getWheelTurn()) < 0.0000001 ? Color.black : (move.getWheelTurn() > 0 ? Color.green : Color.blue);

		Global.s_vc.fillCircle(self.getX() + 150, self.getY() - 100, 25, c);		
	
		Global.s_vc.text(self.getX() - 150, self.getY()-100, Double.toString(Math.sqrt(self.getSpeedX()*self.getSpeedX()+ self.getSpeedY()*self.getSpeedY())), Color.red);
		Global.s_vc.endPost();


		long ms1 = System.currentTimeMillis();
		//if (tickN > 290 && tickN < 310)
		{
			System.out.printf("time %d ms\n", ms1 - ms0);
			//System.out.printf("tickN:%d curr vel (%.5f, %.5f), next vel %s\n", tickN, v_x, v_y,  cp.m_v.toString());
			//System.out.printf("curr power %.5f, next power %.5f\n", self.getEnginePower(), cp.m_power);
			//System.out.printf("cur turn %.5f, next turn %.5f\n", self.getWheelTurn(), cp.m_wheel);
			//System.out.printf("w %.5f, next w %.5f\n", self.getAngularSpeed(), cp.m_w);
			//System.out.printf("angle %.5f, next angle %.5f\n", self.getAngle(), cp.m_angle);



			//draw waypoints
	//		Global.s_vc.beginPre();
			int[][] wpnts = world.getWaypoints();

			entirePath.clear();

			entirePath.addAll(   Global.s_wave.find( (int)(self.getX()) / 800, (int)(self.getY()) / 800, self.getNextWaypointX(), self.getNextWaypointY()));
		
	
			for (int i = waypointsPassed; i < wpnts.length-1; i++)
			{
				
				Vector<Vector2D> vs = Global.s_wave.find(wpnts[i][0],wpnts[i][1], wpnts[i+1][0],wpnts[i+1][1]);
		//		Global.s_vc.text((int)wpnts[i][0] * 800 + 400, (int)wpnts[i][1]  * 800 + 400, Integer.toString(i), Color.red);
			
				//pop init point
				vs.remove(0);

				entirePath.addAll(vs);
				
			}

		//	Global.s_vc.endPre();
			

	//		Global.s_vc.beginPost();
			int show = 4;
			for (Vector2D vec : entirePath)
			{
			//	System.out.printf("%s\n", vec);
				if (show == 0)
					break;
		//		Global.s_vc.fillCircle((int)vec.x() * 800 + 400, (int)vec.y() * 800 + 400, 50, Color.red);
				show--;
			}				
	//		Global.s_vc.endPost();
		}
		tickN = tickN + 1;
	}

	public static void fillMoveFromInputs(Move move, int t, Vector2D turn_input, Vector2D brake_input, int turn_side)
	{
				int tC = (int)((turn_input.x() + turn_input.y()) / 2);//middle tick
				if (t > turn_input.x() && t < tC)
					move.setWheelTurn(1*turn_side);
				else if (t >= tC && t < turn_input.y())
					move.setWheelTurn(1*turn_side);
				else if (t < turn_input.x() || t > turn_input.y())
					move.setWheelTurn(0);

				if (t > brake_input.x() && t < brake_input.y())
					move.setBrake(true);
				else
					move.setBrake(false);
}


}
