import model.Car;
import model.Game;
import model.Move;
import model.World;
import java.util.*;
import java.awt.Color;

public final class TrajBuilder{


	static public List<Vector2D> m_inputs = null;

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
		return res;
	}
	

	static public Vector2D findBestTrajectory(CarProxy cp, Game game)
	{	
		Vector2D res = new Vector2D(0,0);
		double bestMetric = 0;
		int tickAhead = 200;
		if (m_inputs == null)
			m_inputs = generateInputs(0, tickAhead, 10);
		Global.s_vc.beginPost();
	
		
		//
		for (Vector2D input : m_inputs)
		{
			double wayR = tryTurn(game, cp, input, 1, tickAhead);
			
			double wayL = tryTurn(game, cp, input, -1, tickAhead);

			
		
			if (Math.max(wayR, wayL) > bestMetric)
			{
				bestMetric = Math.max(wayR, wayL);
				
				res = new Vector2D( input.x(),  wayR > wayL ? 1:-1);
			}

		}
		Global.s_vc.endPost();
		return res;
	} 

	static public double tryTurn(Game game, CarProxy clear_cp, Vector2D input, int isRight, int tickAhead) // right 1, left -1
	{
			CarProxy cp = new CarProxy(clear_cp);
			
				//System.out.printf("%s ", input.toString());
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

				PhysSym.step(cp, game);
				if (CollisionChecker.checkBoard(game, cp.m_pos, cp.m_angle))
					{
					//System.out.printf("break \n");
					break;
					}				
				way += (cp.m_pos.sub(oldPos)).length();
				oldPos = cp.m_pos;


			//Global.s_vc.fillCircle((int)cp.m_pos.x(), (int)cp.m_pos.y(), 2, Color.black);				
			}
			return way;
}
}
