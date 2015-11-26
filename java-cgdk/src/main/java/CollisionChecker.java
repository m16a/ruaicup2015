import model.Car;
import model.Game;
import model.Move;
import model.World;
import java.util.*;

public final class CollisionChecker{


	static public boolean checkBoard(Game game, Vector2D car_center)
	{
		boolean res = false;
		Vector2D tile = MyStrategy.getTile(car_center.x(), car_center.y());
		int tileType = Global.s_wave.getNeighbours((int)tile.x(), (int)tile.y());

		Vector2D car_c_rel = car_center.sub(tile.scale(MyStrategy.WIDTH));
		int r = (int)Math.min(game.getCarHeight(), game.getCarWidth());
		
		int offst = 80 + r/2 - 2;//patched

		//System.out.printf(" %s %d", car_c_rel.toString(), offst);
		if ((tileType & Wave.TOP) == 0)
			res |= car_c_rel.y() < offst;

		if ((tileType & Wave.BOTTOM) == 0)
			res |= car_c_rel.y() > MyStrategy.WIDTH-offst;

		if ((tileType & Wave.LEFT) == 0)
			res |= car_c_rel.x() < offst;

		if ((tileType & Wave.RIGHT) == 0)
			res |= car_c_rel.x() > MyStrategy.WIDTH-offst;

		return res;
	}

}
