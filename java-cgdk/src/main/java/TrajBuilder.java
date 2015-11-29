import model.Car;
import model.Game;
import model.Move;
import model.World;
import java.util.*;
import java.awt.Color;

public final class TrajBuilder{


	static public List<Vector2D> m_turn_inputs = null;
	static public List<Vector2D> m_break_inputs = null;


	static public int TURN_VAR = 10;
	static public int BREAK_VAR = 2;	
	static public int TICK_AHEAD = 150;
	static public List<Vector2D> generateInputs(int startTick, int tickAhead, int N) // tickAhead * N ~ 10000
	{
		List<Vector2D> res = new ArrayList<Vector2D>(); 
		int vs = (int)Math.sqrt(2 * N); // number of Variations

		for (int v = 0; v < vs; v++)
		{	
			int firstParam = startTick + v * (tickAhead)/ vs;
			for (int sub_v = 1; sub_v < vs-v; sub_v++)
			{
				int secondParam = firstParam + sub_v * (startTick+tickAhead - firstParam)/(vs-v);
				System.out.printf("[%d %d]", firstParam, secondParam);
				res.add(new Vector2D(firstParam, secondParam));
			}

			System.out.printf("\n");

			//break;
		}
		
		res.add(new Vector2D(startTick+tickAhead, startTick+tickAhead));
		return res;
	}
	
	static public List<Vector2D> generateBrakeInputs(int startTick, int tickAhead, int N) // tickAhead * N ~ 10000
	{
		List<Vector2D> res = new ArrayList<Vector2D>(); 

		if (false)		
		for (int i = 0; i < N; i++)
		{	
			int firstParam = startTick + i * (tickAhead / 1)/ N;
				int secondParam = firstParam + 50;
				System.out.printf("[%d %d]", firstParam, secondParam);
				res.add(new Vector2D(firstParam, secondParam));
			}
		
		System.out.printf("\n");
		res.add(new Vector2D(startTick+tickAhead, startTick+tickAhead));

		res.add(new Vector2D(startTick, startTick+tickAhead));
		return res;
	}
	
	static public Vector2D[] findBestTrajectory(CarProxy cp, Game game)
	{	
		Vector2D[] res = new Vector2D[3];
				res[0] = new Vector2D();
				res[1] = new Vector2D();
				res[2] = new Vector2D();
		double bestMetric = 0;
		int tickAhead = TICK_AHEAD;

		Vector2D best_in_turn = new Vector2D();
		Vector2D best_in_brake = new Vector2D();
		int best_side = 0;

		if (m_turn_inputs == null)
			m_turn_inputs = generateInputs(0, tickAhead, TURN_VAR);

		if (m_break_inputs == null)
			m_break_inputs = generateBrakeInputs(0, tickAhead, BREAK_VAR);
	
	
		Global.s_vc.beginPre();
		
		for (Vector2D turn_input : m_turn_inputs)
			for (Vector2D break_input : m_break_inputs)
		{
			double wayR = tryTurn(game, cp, turn_input, break_input, 1, tickAhead, Color.black, 2);
			
			double wayL = tryTurn(game, cp, turn_input, break_input, -1, tickAhead, Color.black, 2);

			
		
			if (Math.max(wayR, wayL) > bestMetric)
			{
				bestMetric = Math.max(wayR, wayL);
				
				//res = new Vector2D( turn_input.x(),  wayR > wayL ? 1:-1);
				res[0] = turn_input;
				res[1] = new Vector2D(wayR > wayL ? 1:-1, 0);
				res[2] = break_input;
				
				best_in_turn = turn_input;
				best_in_brake = break_input;
				best_side = (int)res[1].x();
			}

		}

		//draw best traj
		tryTurn(game, cp, best_in_turn, best_in_brake, best_side, tickAhead, Color.red,6);
		Global.s_vc.endPre();				
		return res;
	} 

	static public double tryTurn(Game game, CarProxy clear_cp, Vector2D input, Vector2D b_input, int isRight, int tickAhead, Color c, int draw_width) // right 1, left -1
	{
			CarProxy cp = new CarProxy(clear_cp);
			Move move = new Move();	
			//System.out.printf("%s %s\n", input.toString(), b_input.toString());
			double metric = 0;
			Vector2D oldPos = clear_cp.m_pos;
			
			for (int i = 0; i < tickAhead; i++)
			{
				MyStrategy.fillMoveFromInputs(move, i, input, b_input, isRight);
	/*			int tC = (int)((input.x() + input.y()) / 2);//middle tick
				if (i > input.x() && i < tC)
					cp.m_in_wheel = 1*isRight;
				else if (i >= tC && i < input.y())
					cp.m_in_wheel = -1*isRight;
				else if (i < input.x() || i > input.y())
					cp.m_in_wheel = 0;

				if (i > b_input.x() && i < b_input.y())
					cp.m_in_brake = true;
				else
					cp.m_in_brake = false;
*/
				cp.m_in_wheel = move.getWheelTurn();
				cp.m_in_brake = move.isBrake();	

				PhysSym.step(cp, game);
			
				metric += (cp.m_pos.sub(oldPos)).length();
				oldPos = cp.m_pos;


				if (CollisionChecker.checkBoard(game, cp.m_pos, cp.m_angle))
				{
					//System.out.printf("break \n");
					break;
				}				
				Global.s_vc.fillCircle((int)cp.m_pos.x(), (int)cp.m_pos.y(), draw_width, c);
			}
/*			
			int i = getClosestWaypoint(cp.m_pos);
			int ii = i+1;
			if (ii >= MyStrategy.entirePath.size())
				ii = MyStrategy.entirePath.size() - 1;
			if (ii < 0){
					ii = 0;
			}
			Vector2D nextTile = MyStrategy.entirePath.elementAt(ii);
			double dist_to_next_tile = (nextTile.scale(800).add(new Vector2D(400,400) ) ).sub(cp.m_pos).length();
			metric = i * 10000 +  4000 - dist_to_next_tile;

*/

				int i = getClosestWaypoint(cp.m_pos);
				int ii = i+1;
				if (ii >= MyStrategy.entirePath.size())
					ii = MyStrategy.entirePath.size() - 1;
				if (ii > 0)
		{
			
				Vector2D tile = MyStrategy.entirePath.elementAt(ii-1);
				Vector2D nextTile = MyStrategy.entirePath.elementAt(ii);

				Vector2D arr = nextTile.sub(tile);

				if (MyStrategy.arrows.size() > 1)
				{
					int cc = 0;
					for (cc = 0; MyStrategy.arrows.elementAt(cc) == Wave.TOP; cc++){}
					
					if (MyStrategy.arrows.elementAt(cc) == Wave.RIGHT)
						arr = arr.rotate(-3.14f / 2);
					else if (MyStrategy.arrows.elementAt(cc) == Wave.LEFT)
					{
						arr = arr.rotate(3.14f / 2);
									
						//System.out.printf("ROT LEFT");	
					}
				//System.out.printf("cc %d\n", cc);	
				}
				
			Vector2D p = (nextTile.scale(800).add(new Vector2D(400,400) ) ).add(arr.scale(300));
			double dist_to_next_tile = p.sub(cp.m_pos).length();

	
			Global.s_vc.fillCircle(p.x(), p.y(), 25, Color.green);		
		metric = i * 10000 +  4000 - dist_to_next_tile;
		}
			
			return metric;
}

	static public int getClosestWaypoint(Vector2D pos)
	{
		int res = 0;
		int hack = 4;
		for (Vector2D v : MyStrategy.entirePath)
		{
				if (hack == 0)
					return 0;
				if ( (int)(pos.x()) / 800 == (int)v.x() && (int)(pos.y()) / 800 == (int)v.y())
					break;
				res++;
				hack--;
		}
		//System.out.printf("EXTRA %d\n", res);
		return res;	
	}


}
