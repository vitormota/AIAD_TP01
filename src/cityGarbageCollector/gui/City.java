package cityGarbageCollector.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import util.custom.StretchIcon;
import cityGarbageCollector.Edge;
import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import cityGarbageCollector.RoadMap;
import cityGarbageCollector.RoadMap.Direction;
import cityGarbageCollector.RoadMap.Road;
import cityGarbageCollector.RoadMap.Road_Type;
import cityGarbageCollector.Vertex;

public class City extends JPanel {

	public static JLabel lastSelected = null;

	/**
	 * City size in blocks width
	 */
	private int size_w;
	/**
	 * City size in blocks height
	 */
	private int size_h;

	/**
	 * the city grid
	 */
	private Gridpanel[][] city_squares;

	/**
	 * The city map (graph)
	 */
	private RoadMap map;

	/**
	 * Start a new instance of the city road map, witch is a grid
	 * 
	 * @param width
	 *            in blocks
	 * @param height
	 *            in blocks
	 */
	public City(int width, int height) {
		this.size_w = width;
		this.size_h = height;
		city_squares = new Gridpanel[getSize_h()][getSize_w()];
	}

	/**
	 * Start a new instance of the city road map, witch is a grid
	 * with roads specified on a file
	 * 
	 * @param width
	 *            in blocks
	 * @param height
	 *            in blocks
	 * @param map_file
	 *            the file containing the city map
	 * @throws FileNotFoundException
	 */
	public City(String map_file) throws FileNotFoundException {
		this.map = new RoadMap(map_file);
		this.size_w = map.sizeX;
		this.size_h = map.sizeY;
		city_squares = new Gridpanel[getSize_h()][getSize_w()];
	}

	public LinkedList<Location> getAgentTrip(Location loc) {
		LinkedList<Vertex> verts = (LinkedList<Vertex>) map.getAgentCircuit(map.getVertexByLocation(loc));
		//LinkedList<Vertex> verts = map.getAgentCircuit(map.getVertexByLocation(loc));
		LinkedList<Location> res = new LinkedList<>();
		for (Vertex v : verts) {
			res.add(v.copyLocation());
		}
		return res;
	}

	public LinkedList<Location> getAgentTrip(Location pos, Location dest) {
		LinkedList<Vertex> verts = (LinkedList<Vertex>) map.getDirections(pos, dest);
		LinkedList<Location> res = new LinkedList<>();
		for (Vertex v : verts) {
			res.add(v.copyLocation());
		}
		return res;
	}

	public void modifyMap(Road_Type type, int x, int y) {
		Gridpanel up = city_squares[y-1][x], down = city_squares[y+1][x], right = city_squares[y][x+1], left = city_squares[y][x-1];
		Vertex v = new Vertex(new Location(x, y));
		map.addVertice(v);
		Vertex side;
		switch (type) {
		case One_West:
			city_squares[y][x].oneway_road_west = true;
			if (up.oneway_road_south || up.twoway_road_vert) {
				addConnection(v, map.getVertexByLocation(up.toLoc()), up.twoway_road_vert);
			} else if (up.oneway_road_north) {
				addConnection(map.getVertexByLocation(up.toLoc()), v, false);
			}
			if (down.oneway_road_north || down.twoway_road_vert) {
				addConnection(v, map.getVertexByLocation(down.toLoc()), down.twoway_road_vert);
			} else if (down.oneway_road_south) {
				addConnection(map.getVertexByLocation(down.toLoc()), v, false);
			}
			if (right.hasRoad() && !right.oneway_road_east) {
				addConnection(v, map.getVertexByLocation(right.toLoc()), false);
			}
			addConnection(map.getVertexByLocation(left.toLoc()), v, false);
			break;
		case One_North:
			city_squares[y][x].oneway_road_north = true;
			addConnection(map.getVertexByLocation(up.toLoc()), v, false);
			if (!down.oneway_road_south) {
				addConnection(v, map.getVertexByLocation(down.toLoc()), false);
			}
			if (right.oneway_road_west || right.twoway_road) {
				addConnection(v, map.getVertexByLocation(right.toLoc()), right.twoway_road);
			}
			if (left.oneway_road_east || left.twoway_road) {
				addConnection(v, map.getVertexByLocation(left.toLoc()), left.twoway_road);
			}
			break;
		case One_South:
			city_squares[y][x].oneway_road_south = true;
			if (!up.oneway_road_north) {
				addConnection(v, map.getVertexByLocation(up.toLoc()), false);
			}
			addConnection(map.getVertexByLocation(down.toLoc()), v, false);
			if (right.oneway_road_west || right.twoway_road) {
				addConnection(v, map.getVertexByLocation(right.toLoc()), right.twoway_road);
			}
			if (left.oneway_road_east || left.twoway_road) {
				addConnection(v, map.getVertexByLocation(left.toLoc()), left.twoway_road);
			}
			break;
		case One_East:
			city_squares[y][x].oneway_road_east = true;
			if (up.oneway_road_south || up.twoway_road_vert) {
				addConnection(v, map.getVertexByLocation(up.toLoc()), up.twoway_road_vert);
			} else if (up.oneway_road_north) {
				addConnection(map.getVertexByLocation(up.toLoc()), v, false);
			}
			if (down.oneway_road_north || down.twoway_road_vert) {
				addConnection(v, map.getVertexByLocation(down.toLoc()), down.twoway_road_vert);
			} else if (down.oneway_road_south) {
				addConnection(map.getVertexByLocation(down.toLoc()), v, false);
			}
			addConnection(map.getVertexByLocation(right.toLoc()), v, left.twoway_road);
			if (left.hasRoad() && !left.oneway_road_west) {
				addConnection(v, map.getVertexByLocation(left.toLoc()), false);
			}
			break;
		case Two_hor:
			city_squares[y][x].twoway_road = true;
			break;
		case Two_vert:
			city_squares[y][x].twoway_road_vert = true;
			break;
		default:
			break;
		}

		try {
			city_squares[y][x].updateImage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// map.append(type,x,y);
	}

	private void addConnection(Vertex v, Vertex v1, boolean twoway_road) {
		// TODO Auto-generated method stub
		if(v == null || v1 == null) return;
		v1.addEdge(new Edge(v1, v));
		if (twoway_road)
			v1.addEdge(new Edge(v, v1));
	}

	/**
	 * version 0.1 draw a simple grid
	 */
	public void drawGrid() {
		setLayout(new GridLayout(getSize_w(), getSize_h()));
		for (int i = 0; i < getSize_h(); i++) {
			for (int j = 0; j < getSize_w(); j++) {
				city_squares[i][j] = new Gridpanel(j, i);
				add(city_squares[i][j]);
			}
		}
	}

	public void drawMap() {
		// TODO Auto-generated method stub
		Road[] roads = map.getRoads();
		for (Road r : roads) {
			if (r.turn) {
				city_squares[r.location.y][r.location.x].oneway_road = true;
				city_squares[r.location.y][r.location.x].oneway_road_vert = true;
			} else if (r.direction == Direction.NORTH)
				city_squares[r.location.y][r.location.x].oneway_road_north = true;
			else if (r.direction == Direction.SOUTH)
				city_squares[r.location.y][r.location.x].oneway_road_south = true;
			else if (r.direction == Direction.WEST)
				city_squares[r.location.y][r.location.x].oneway_road_west = true;
			else if (r.direction == Direction.EAST)
				city_squares[r.location.y][r.location.x].oneway_road_east = true;
			try {
				city_squares[r.location.y][r.location.x].updateImage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the size_h
	 */
	public int getSize_h() {
		return size_h;
	}

	/**
	 * @return the size_w
	 */
	public int getSize_w() {
		return size_w;
	}

	public Gridpanel getSpaceByLocation(Location l) {
		return city_squares[l.y][l.x];
	}

	public class Gridpanel extends JLabel implements MouseListener {
		public int x, y;
		public Image img;

		/**
		 * Flags for objects on this space
		 */
		public boolean collector = false, burner = false, container = false, oneway_road = false, twoway_road = false,
				oneway_road_vert = false, twoway_road_vert = false, oneway_road_east = false, oneway_road_west = false,
				oneway_road_north = false, oneway_road_south = false;

		public Gridpanel(int x, int y) {
			// TODO Auto-generated constructor stub
			this.x = x;
			this.y = y;
			// this.setBorder(BorderFactory.createLineBorder(Color.RED));
			addMouseListener(this);
		}

		public Location toLoc() {
			return new Location(x, y);
		}

		public boolean hasRoad() {
			return (oneway_road_east || oneway_road_vert || oneway_road || oneway_road_north || oneway_road_south || oneway_road_west || twoway_road || twoway_road_vert);
		}

		public void updateImage() throws IOException {
			if (collector) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/collector_on_road.png")), false));
				return;
			}
			if (burner) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/burner.png")),false));
				return;
			}
			if (container) {
				setIcon(new ImageIcon(ImageIO.read(new File("images/garbage.png"))));
				return;
			}
			if (oneway_road && oneway_road_vert) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/crossroad.png")), false));
				return;
			}
			if (twoway_road && twoway_road_vert) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/crossroad.png")), false));
				return;
			}

			if (oneway_road_east) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/oneway_road_east.png")), false));
				return;
			}
			if (oneway_road_west) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/oneway_road_west.png")), false));
				return;
			}
			if (oneway_road_north) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/oneway_road_north.png")), false));
				return;
			}
			if (oneway_road_south) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/oneway_road_south.png")), false));
				return;
			}

			if (twoway_road) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/twoway_road.png")), false));
				return;
			}
			if (twoway_road_vert) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/twoway_road_vert.png")), false));
				return;
			}
			// if none, then put background back
			setIcon(null);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Clicked on: " + x + "," + y);
			if (lastSelected != null) {
				lastSelected.setBorder(null);
			}
			setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			GCollector.getInstance().getEnv().setAgentPosTextFields(x, y);
			GCollector.getInstance().getEnv().setModifyPosTextFields(x, y);

			lastSelected = (JLabel) e.getComponent();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}

}
