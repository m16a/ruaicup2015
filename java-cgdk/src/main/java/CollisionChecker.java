import model.Car;
import model.Game;
import model.Move;
import model.World;
import java.util.*;

public final class CollisionChecker{
	static Vector2D corner1 = new Vector2D(0, 0);
	static Vector2D corner2 = new Vector2D(MyStrategy.WIDTH, 0);
	static Vector2D corner3 = new Vector2D(0, MyStrategy.WIDTH);
	static Vector2D corner4 = new Vector2D(MyStrategy.WIDTH, MyStrategy.WIDTH);

	static Vector2D[] corners = null;

	static public boolean checkBoard(Game game, Vector2D car_center, double angle)
	{
		boolean res = false;
		if (corners == null)
		{
			Vector2D cc1 = new Vector2D( game.getCarWidth() / 2,  game.getCarHeight() / 2);
			Vector2D cc2 = new Vector2D(-game.getCarWidth() / 2,  game.getCarHeight() / 2);
			Vector2D cc3 = new Vector2D( game.getCarWidth() / 2, -game.getCarHeight() / 2);
			Vector2D cc4 = new Vector2D(-game.getCarWidth() / 2, -game.getCarHeight() / 2);
			
			corners = new Vector2D[2];

			corners[0] = (cc1);
			//corners[1] = (cc2);
			corners[1] = (cc3);
			//corners[3] = (cc4);
		}


		for (Vector2D c : corners)
		{
			Vector2D p = c.rotate(angle).add(car_center);
			
			res |= checkPoint(p);	
		
		//	System.out.printf("%s %.3f %s %b\n", car_center.toString(), angle, p.toString(), res);
			
			if (res)
				break;
		}


		//System.out.printf(" %s %d", car_c_rel.toString(), offst);
		return res;
	}
	static public boolean checkPoint(Vector2D p)
	{
		boolean res = false;

		Vector2D tile = MyStrategy.getTile(p.x(), p.y());
		int tileType = Global.s_wave.getNeighbours((int)tile.x(), (int)tile.y());

		Vector2D p_rel = p.sub(tile.scale(MyStrategy.WIDTH));
		double offst = 82;
		if ((tileType & Wave.TOP) == 0)
			if  (p_rel.y() < offst)
				return true;

		if ((tileType & Wave.BOTTOM) == 0)
			if (p_rel.y() > MyStrategy.WIDTH-offst)
				return true;

		if ((tileType & Wave.LEFT) == 0)
			if (p_rel.x() < offst)
				return true;

		if ((tileType & Wave.RIGHT) == 0)
			if (p_rel.x() > MyStrategy.WIDTH-offst)
				return true;
		
		if ( ((tileType & Wave.BOTTOM) != 0) && ((tileType & Wave.RIGHT) != 0) )
			if (p_rel.sub(corner4).length() < offst)
				return true;

		if ( ((tileType & Wave.BOTTOM) != 0) && ((tileType & Wave.LEFT) != 0) )
			if (p_rel.sub(corner3).length() < offst) 
				return true; 

		if ( ((tileType & Wave.TOP) != 0) && ((tileType & Wave.RIGHT) != 0) )
			if (p_rel.sub(corner2).length() < offst)
				return true;

		if ( ((tileType & Wave.TOP) != 0) && ((tileType & Wave.LEFT) != 0) )
			if (p_rel.sub(corner1).length() < offst)
				return true;



		return res;
	}
}
