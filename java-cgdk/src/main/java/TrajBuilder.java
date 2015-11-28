import model.Car;
import model.Game;
import model.Move;
import model.World;
import java.util.*;
import java.awt.Color;

public final class TrajBuilder{


	static public List<Vector2D> m_turn_inputs = null;
	static public List<Vector2D> m_break_inputs = null;
	
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
		//		System.out.printf("[%d %d]", firstParam, secondParam);
				res.add(new Vector2D(firstParam, secondParam));
			}
		//	System.out.printf("\n");
		}
		
		res.add(new Vector2D(startTick+tickAhead, startTick+tickAhead));
		return res;
	}
	
	static public List<Vector2D> generateBrakeInputs(int startTick, int tickAhead, int N) // tickAhead * N ~ 10000
	{
		List<Vector2D> res = new ArrayList<Vector2D>(); 

		if (true)		
		for (int i = 0; i < N; i++)
		{	
			int firstParam = startTick + i * (tickAhead / 3)/ N;
				int secondParam = firstParam + 30;
		//		System.out.printf("[%d %d]", firstParam, secondParam);
				res.add(new Vector2D(firstParam, secondParam));
		}
		
		res.add(new Vector2D(startTick+tickAhead, startTick+tickAhead));
		return res;
	}
	
	static public Vector2D[] findBestTrajectory(CarProxy cp, Game game)
	{	
		Vector2D[] res = new Vector2D[3];
				res[0] = new Vector2D();
				res[1] = new Vector2D();
				res[2] = new Vector2D();
		double bestMetric = 0;
		int tickAhead = 100;

		Vector2D best_in_turn = new Vector2D();
		Vector2D best_in_brake = new Vector2D();
		int best_side = 0;

		if (m_turn_inputs == null)
			m_turn_inputs = generateInputs(0, tickAhead, 5);

		if (m_break_inputs == null)
			m_break_inputs = generateBrakeInputs(0, tickAhead, 5);
	
	
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

			metric += getClosestWaypoint(cp.m_pos) * 10000;

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
