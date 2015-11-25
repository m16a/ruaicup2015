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
				//System.out.printf("[%d %d]", firstParam, secondParam);
				res.add(new Vector2D(firstParam, secondParam));
			}
			//System.out.printf("\n");
		}
		return res;
	}
	

	static public int findBestTrajectory(CarProxy clear_cp, Game game)
	{	
		int tickAhead = 200;
		if (m_inputs == null)
			m_inputs = generateInputs(0, tickAhead, 25);
		Global.s_vc.beginPost();
		for (Vector2D input : m_inputs)
		{
			CarProxy cp = new CarProxy(clear_cp);
			
			for (int i = 0; i < tickAhead; i++)
			{
				int tC = (int)((input.x() + input.y()) / 2);//middle tick
				if (i > input.x() && i < tC)
					cp.m_in_wheel = 1;
				else if (i >= tC && i < input.y())
					cp.m_in_wheel = -1;
				else if (i < input.x() && i > input.y())
					cp.m_in_wheel = 0;

				PhysSym.step(cp, game);
				Global.s_vc.fillCircle((int)cp.m_pos.x(), (int)cp.m_pos.y(), 2, Color.black);				
			}
		}
		Global.s_vc.endPost();
		return 0;
	} 
}
