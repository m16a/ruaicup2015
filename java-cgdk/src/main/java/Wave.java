import model.Car;
import model.Game;
import model.Move;
import model.World;
import java.util.*;
import model.TileType;

public final class Wave
{
	static public int TOP = 8;//1<<3;
	static public int BOTTOM = 4;//1<<2;
	static public int LEFT = 2;//1<<1;
	static public	int RIGHT = 1;// 1<<0;
	
	private int m_width;
	private int m_height;
	
	public Node[][] m_nodes; 

	public Wave(World w)
	{
		this.m_width = w.getWidth();
		this.m_height = w.getHeight();
		this.m_w = w;
		this.m_nodes = new Node[m_width][m_height]; 
	
		for(int i = 0; i < m_width; i++)
			for (int j = 0; j < m_height; j++)
				this.m_nodes[i][j] = new Node();
	}

	public World m_w;

	public final class Node
	{	
		int d = -1;

		public Node(){
			this.d = -1;
		}
	}


	public Vector<Vector2D> find(int startI, int startJ, int finishI, int finishJ)
	{
		//forward
		int dist = 0;


	 //	System.out.printf("find %d %d %d %d\n", m_width, m_height, startI, startJ);
		m_nodes[startI][startJ].d = dist;
		
		while (m_nodes[finishI][finishJ].d == -1)// and ability to build wave
		{
			for (int i = 0; i < m_w.getWidth(); i++)
				for (int j = 0; j < m_w.getHeight(); j++)
				{
					if (m_nodes[i][j].d != dist) //unvisited node + node with different age
						continue;
					
					int ns = getNeighbours(i,j);

					if ((ns & TOP) != 0 && j > 0)
						if (m_nodes[i][j-1].d == -1) //unvisited
							m_nodes[i][j-1].d = dist + 1;

					if ((ns & BOTTOM) != 0 && j < m_height - 1)	
						if (m_nodes[i][j+1].d == -1) //unvisited
							m_nodes[i][j+1].d = dist + 1;

					if ((ns & LEFT) != 0 && i > 0)
						if (m_nodes[i-1][j].d == -1) //unvisited
							m_nodes[i-1][j].d = dist + 1;

					if ((ns & RIGHT) != 0 && i < m_width -1)				
							if (m_nodes[i+1][j].d == -1) //unvisited
							m_nodes[i+1][j].d = dist + 1;
				}

			dist++;
		}

			//for(int i = 0; i < m_width; i++){
			//for (int j = 0; j < m_height; j++)
			//	System.out.printf("%d\t", m_nodes[i][j].d);
			//	System.out.printf("\n");
			//}

		//backward
		Vector res = new Vector();
		
		int nI = finishI;
		int nJ = finishJ;

		res.addElement(new Vector2D(nI,nJ));
		while (nI != startI || nJ != startJ)
		{
			int d = m_nodes[nI][nJ].d;

			if (nI > 0 && m_nodes[nI-1][nJ].d == d-1){
				res.insertElementAt(new Vector2D(nI-1, nJ),0);
				nI--;
				d--;
				continue;	
			}
			
			if (nI < m_width - 1 && m_nodes[nI+1][nJ].d == d-1){
				res.insertElementAt(new Vector2D(nI+1, nJ),0);
				nI++;
				d--;
				continue;	
			}	
			
			if (nJ > 0 && m_nodes[nI][nJ-1].d == d-1){
				res.insertElementAt(new Vector2D(nI, nJ-1),0);
				nJ--;
				d--;
				continue;	
			}	

			if (nJ < m_height-1 && m_nodes[nI][nJ+1].d == d-1){
				res.insertElementAt(new Vector2D(nI, nJ+1),0);
				nJ++;
				d--;
				continue;	
			}
		}

		return res;	
	}

	public int getNeighbours(int x, int y)
	{
		int res = 0;

		TileType t  = m_w.getTilesXY()[x][y];	
		switch (t)
		{
			case VERTICAL:// Тайл с прямым вертикальним участком дороги.
				res |= TOP | BOTTOM; 
				break;
     
      case  HORIZONTAL:// Тайл с прямым горизонтальным участком дороги.
				res |= RIGHT | LEFT;
				break;

			case LEFT_TOP_CORNER:
				res |= RIGHT | BOTTOM;// Тайл, выполняющий роль сочленения двух других тайлов: справа и снизу от данного тайла.
				break;
			case RIGHT_TOP_CORNER:// Тайл, выполняющий роль сочленения двух других тайлов: слева и снизу от данного тайла.
				res |= LEFT | BOTTOM;
				break;
    /**
     * Тайл, выполняющий роль сочленения двух других тайлов: справа и сверху от данного тайла.
     */
			case LEFT_BOTTOM_CORNER:
				res |= RIGHT | TOP;
				break;

    /**
     * Тайл, выполняющий роль сочленения двух других тайлов: слева и сверху от данного тайла.
     */
			case RIGHT_BOTTOM_CORNER:
				res |= LEFT | TOP;
				break;
    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: слева, снизу и сверху от данного тайла.
     */
			case LEFT_HEADED_T:
				res |= LEFT | BOTTOM | TOP;
				break;
    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: справа, снизу и сверху от данного тайла.
     */
			case RIGHT_HEADED_T:
				res |= RIGHT | BOTTOM | TOP;
				break;
    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: слева, справа и сверху от данного тайла.
     */
			case TOP_HEADED_T:
				res |= LEFT | RIGHT | TOP;
				break;
    /**
     * Тайл, выполняющий роль сочленения трёх других тайлов: слева, справа и снизу от данного тайла.
     */
			case BOTTOM_HEADED_T:
				res |= LEFT | RIGHT | BOTTOM;
				break;
    /**
     * Тайл, выполняющий роль сочленения четырёх других тайлов: со всех сторон от данного тайла.
     */
			case CROSSROADS:

				res |= LEFT | RIGHT | BOTTOM | TOP;
				break;
		}
		return res;
	}
}
