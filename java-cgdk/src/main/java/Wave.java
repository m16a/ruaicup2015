import model.Car;
import model.Game;
import model.Move;
import model.World;


public final class Wave
{
	int TOP = 1<<3;
	int BOTTOM = 1<<2;
	int LEFT = 1<<1;
	int RIGHT = 1<<0;
	public void Vawe(World w)
	{
		this.m_w = w;
		this.m_nodes[][] = new Node[w.getWidth()][w.getHeight()]; 
	}

	public World m_w;

	public final class Node
	{	
		int d = -1;
	}

	public Node[][] m_nodes = null; 

	public Vector find(int startI, int startJ, int finishI, int finishJ)
	{
		//forward
		int dist = 0;
		m_nodes[startI][startJ].d = dist;
		
		while (m_nodes[finishI][finishJ].d == -1)// and ability to build wave
		{
			for (int i = 0; i < w.getWidth(); i++)
				for (int j = 0; j < w.getHeight(); j++)
				{
					if (m_nodes[i][j].d != dist) //unvisited node + node with different age
						continue;
					
					int ns = getNeighbours(i,j);

					if (ns & TOP)
						if (m_nodes[i][j-1].d == -1) //unvisited
							m_nodes[i][j-1].d = dist + 1;

					if (ns & BOTTOM)	
						if (m_nodes[i][j+1].d == -1) //unvisited
							m_nodes[i][j+1].d = dist + 1;

					if (ns & LEFT)
						if (m_nodes[i-1][j].d == -1) //unvisited
							m_nodes[i-1][j].d = dist + 1;

					if (ns & RIGHT)				
							if (m_nodes[i+1][j].d == -1) //unvisited
							m_nodes[i+1][j].d = dist + 1;
				}

			dist++;
		}

		//backward
		Vector res = new Vector();
		
		int nI = finishI;
		int nJ = finishJ;

		res.addElement(new Vector2D(nI,nJ));
		while (nI != startI && nI != startJ)
		{
			int d = m_nodes[nI][nJ].d;

			if (m_nodes[nI-1][nJ].d == d-1){
				res.addElement(new Vector(nI-1, nJ));
				nI--;
				continue;	
			}
			
			if (m_nodes[nI+1][nJ].d == d-1){
				res.addElement(new Vector(nI+1, nJ));
				nI++;
				continue;	
			}	
			
			if (m_nodes[nI][nJ-1].d == d-1){
				res.addElement(new Vector(nI, nJ-1));
				nJ--;
				continue;	
			}	

			if (m_nodes[nI][nJ+1].d == d-1){
				res.addElement(new Vector(nI, nJ+1));
				nJ++;
				continue;	
			}
		}

		return res;	
	}

	private int getNeighbours(int x, int y)
	{
		int res = 0;

		TileType t  = m_w.getTilesXY()x[][y];	
		switch (t)
		{
			case  VERTICAL:// Тайл с прямым вертикальним участком дороги.
				res = TOP | BOTTOM; 
				break;
     
      case HORIZONTAL:// Тайл с прямым горизонтальным участком дороги.
				res = RIGHT | LEFT;
				break;

			case LEFT_TOP_CORNER:
				res = RIGHT | BOTTOM:// Тайл, выполняющий роль сочленения двух других тайлов: справа и снизу от данного тайла.
				break;
			case RIGHT_TOP_CORNER:// Тайл, выполняющий роль сочленения двух других тайлов: слева и снизу от данного тайла.
				res = LEFT | BOTTOM:
				break;
    /**
     * Тайл, выполняющий роль сочленения двух других тайлов: справа и сверху от данного тайла.
     */
			case LEFT_BOTTOM_CORNER:
				res = RIGHT | TOP;
				break;

    /**
     * Тайл, выполняющий роль сочленения двух других тайлов: слева и сверху от данного тайла.
     */
			case RIGHT_BOTTOM_CORNER:
				res = LEFT | TOP;
				break;
    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: слева, снизу и сверху от данного тайла.
     */
			case LEFT_HEADED_T:
				res = LEFT | BOTTOM | TOP;
				break;
    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: справа, снизу и сверху от данного тайла.
     */
			case RIGHT_HEADED_T:
				res = RIGHT | BOTTOM | TOP;
				break;
    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: слева, справа и сверху от данного тайла.
     */
			case TOP_HEADED_T:
				res = LEFT | RIGHT | TOP;
				break;
    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: слева, справа и снизу от данного тайла.
     */
			case BOTTOM_HEADED_T:
				res = LEFT | RIGHT | BOTTOM;
				break;
    /**
     * Тайл, выполняющий роль сочленения четырёх других тайлов: со всех сторон от данного тайла.
     */
			case CROSSROADS:

				res = LEFT | RIGHT | BOTTOM | TOP;
				break;
		}
		return res;
	}
}
