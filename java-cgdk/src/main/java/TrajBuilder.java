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
	

	static public double[] findBestTrajectory(CarProxy cp, Game game)
	{	
		double[] res = new double[3];
				res[0] = 0;
				res[1] = 0;
				res[2] = 0;
		double bestMetric = 0;
		int tickAhead = 150;

		Vector2D best_in_turn = new Vector2D();
		Vector2D best_in_brake = new Vector2D();
		int best_side = 0;

		if (m_turn_inputs == null)
			m_turn_inputs = generateInputs(0, tickAhead, 10);

		if (m_break_inputs == null)
			m_break_inputs = generateInputs(0, tickAhead, 10);
	
	
			Global.s_vc.beginPre();
		
		//
		for (Vector2D turn_input : m_turn_inputs)
			for (Vector2D break_input : m_break_inputs)
		{
			double wayR = tryTurn(game, cp, turn_input, break_input, 1, tickAhead, Color.black, 2);
			
			double wayL = tryTurn(game, cp, turn_input, break_input, -1, tickAhead, Color.black, 2);

			
		
			if (Math.max(wayR, wayL) > bestMetric)
			{
				bestMetric = Math.max(wayR, wayL);
				
				//res = new Vector2D( turn_input.x(),  wayR > wayL ? 1:-1);
				res[0] = turn_input.x();
				res[1] = wayR > wayL ? 1:-1;
				res[2] = break_input.x();
				
				best_in_turn = turn_input;
				best_in_brake = break_input;
				best_side = (int)res[1];
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
			
			//System.out.printf("%s %s\n", input.toString(), b_input.toString());
			double way = 0;
			Vector2D oldPos = clear_cp.m_pos;
			for (int i = 0; i < tickAhead; i++)
			{
				int tC = (int)((input.x() + input.y()) / 2);//middle tick
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

		

				PhysSym.step(cp, game);
				if (CollisionChecker.checkBoard(game, cp.m_pos, cp.m_angle))
					{
					//System.out.printf("break \n");
					break;
					}				
				way += (cp.m_pos.sub(oldPos)).length();
				oldPos = cp.m_pos;

				Global.s_vc.fillCircle((int)cp.m_pos.x(), (int)cp.m_pos.y(), draw_width, c);
			}
			return way;
}
}
