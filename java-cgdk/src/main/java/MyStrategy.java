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
	public static Vector2D acc_input = new Vector2D();
	public static int turn_side = 0;
	public static boolean inputReady = false;
	
	public static int waypointsPassed = 0;
	public static int nextX = -1;
	public static int nextY = -1;

	public static Vector2D getTile(double x, double y)
	{
		return new Vector2D((int)x / WIDTH, (int)y / WIDTH);
	}
	
	public static Vector2D old_pos = new Vector2D(0,0);
	public static int old_pos_cntr = 0;
	public static int move_back_duration = 0;
	
	public static Vector<Vector2D> entirePath = new Vector<Vector2D>();

	public static Vector<Integer> arrows = new Vector<Integer>();
  @Override
	public void move(Car self, World world, Game game, Move move) 
	{

		if (Global.DBG_RNDR)
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
	


		//-------------- Check if stacked ------------------
		Vector2D cur_pos = new Vector2D(self.getX(), self.getY());
	
		if (cur_pos.sub(old_pos).length() < 0.05 && tickN > 200)
			old_pos_cntr++;
		
		old_pos = cur_pos;


		if (old_pos_cntr > 50 )
		{
			move_back_duration = 100; 
			old_pos_cntr = 0;
		}

		if (move_back_duration > 0 )
		{
			move_back_duration--;
			moveBack(move);
			return;
		}
		//-------------------------------------------------

	
		CarProxy cp = new CarProxy(self, game);
		//double in_p = 1.0D;	
		//move.setEnginePower(in_p);
		//cp.m_in_power = in_p;
				
	
		if (tickN > 170 && (tickN % 5) == 0)
		{		
			Vector2D[] input = TrajBuilder.findBestTrajectory(cp, game);
			turn_input = input[0].add(new Vector2D(tickN, tickN));
			turn_side = (int)(input[1].x());
			brake_input = input[2].add(new Vector2D(tickN, tickN));		
			acc_input = input[3].add(new Vector2D(tickN, tickN));	
			inputReady = true;
		}	
		
		if (inputReady)
			fillMoveFromInputs(move, tickN, turn_input, brake_input, turn_side, acc_input);


		//------------- Draw car parameters ---------------- 
		if (Global.DBG_RNDR)
		{
			Global.s_vc.beginPre();
			Global.s_vc.fillCircle(self.getX() + 200, self.getY() - 100, 25, move.isBrake() ? Color.red :Color.black);		


			Color c = Math.abs(move.getWheelTurn()) < 0.0000001 ? Color.black : (move.getWheelTurn() > 0 ? Color.green : Color.blue);

			Global.s_vc.fillCircle(self.getX() + 150, self.getY() - 100, 25, c);		

			Color cc = Math.abs(move.getEnginePower()) < 0.0000001 ? Color.black : (move.getEnginePower() > 0 ? Color.green : Color.blue);

			Global.s_vc.fillCircle(self.getX() + 250, self.getY() - 100, 25, cc);
				
			Global.s_vc.text(self.getX() - 150, self.getY()-100, Double.toString(Math.sqrt(self.getSpeedX()*self.getSpeedX()+ self.getSpeedY()*self.getSpeedY())), Color.red);
			Global.s_vc.endPre();
			}
		//--------------------------------------------------


		long ms1 = System.currentTimeMillis();
		//if (tickN > 290 && tickN < 310)
		{
			//System.out.printf("time %d ms\n", ms1 - ms0);
			//System.out.printf("tickN:%d curr vel (%.5f, %.5f), next vel %s\n", tickN, v_x, v_y,  cp.m_v.toString());
			//System.out.printf("curr power %.5f, next power %.5f\n", self.getEnginePower(), cp.m_power);
			//System.out.printf("cur turn %.5f, next turn %.5f\n", self.getWheelTurn(), cp.m_wheel);
			//System.out.printf("w %.5f, next w %.5f\n", self.getAngularSpeed(), cp.m_w);
			//System.out.printf("angle %.5f, next angle %.5f\n", self.getAngle(), cp.m_angle);

			//draw waypoints
		//	Global.s_vc.beginPre();
			int[][] wpnts = world.getWaypoints();

			entirePath.clear();

			entirePath.addAll(   Global.s_wave.find( (int)(self.getX()) / 800, (int)(self.getY()) / 800, self.getNextWaypointX(), self.getNextWaypointY()));
		
	
			for (int i = waypointsPassed; i < wpnts.length+1; i++)
			{
				Vector<Vector2D> vs = Global.s_wave.find(wpnts[i % wpnts.length ][0],wpnts[i % wpnts.length ][1], wpnts[(i+1) % wpnts.length][0],wpnts[(i+1) % wpnts.length][1]);
	//			Global.s_vc.text((int)wpnts[i % wpnts.length][0] * 800 + 400, (int)wpnts[i % wpnts.length][1]  * 800 + 400, Integer.toString(i % wpnts.length), Color.red);
			
				//pop init point
				vs.remove(0);
				entirePath.addAll(vs);
			}
//			Global.s_vc.endPre();
			
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

			// define turns

			//Vector<Integer> arrow = new Vector<Integer>();	
			arrows.clear();
			for (int i = 0; i < entirePath.size()-2; i++)
			{
				Vector2D v1 = entirePath.elementAt(i+1).sub(entirePath.elementAt(i));
				Vector2D v2 = entirePath.elementAt(i+2).sub(entirePath.elementAt(i+1)); 
					
				if (v1.dot(v2) < 0.01)
				{
					if (v1.cross(v2) > 0)
						arrows.add(Wave.RIGHT);
					else
						arrows.add(Wave.LEFT);
				}
				else if (v1.add(v2).length() < 0.01)
						arrows.add(Wave.BOTTOM);
				else 
						arrows.add(Wave.TOP);
			}

	/*		
		for (Integer i : arrows)
			switch (i)
			{
				case Wave.TOP:
					System.out.printf("forward ");
				break;
				case Wave.LEFT:
					System.out.printf("left ");
				break;
				case Wave.RIGHT:
					System.out.printf("right ");
				break;
				case Wave.BOTTOM:
					System.out.printf("backward ");
				break;

			}
			
		System.out.printf("\n ");
*/

		}
		

		tickN = tickN + 1;
	}

	public static void fillMoveFromInputs(Move move, int t, Vector2D turn_input, Vector2D brake_input, int turn_side, Vector2D acc_input)
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

		if (t > Math.abs(acc_input.x()) && t < acc_input.y())
			move.setEnginePower( (turn_input.x() >= 0 ? 1.0D : -1.D ));	
		else 
			move.setEnginePower(0.0D);	
	}

	public static void moveBack(Move move)
	{
		move.setEnginePower(-1.0D);
		move.setWheelTurn(1 * turn_side);
		move.setBrake(false);
	}
}
