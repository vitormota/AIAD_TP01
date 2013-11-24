package cityGarbageCollector.gui;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import util.custom.StretchIcon;
import cityGarbageCollector.Edge;
import cityGarbageCollector.Location;
import cityGarbageCollector.RoadMap;
import cityGarbageCollector.RoadMap.Direction;
import cityGarbageCollector.RoadMap.Road;
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
		LinkedList<Vertex> verts = map.getAgentCircuit(map.getVertexByLocation(loc));
		LinkedList<Location> res = new LinkedList<>();
		for (Vertex v : verts) {
			res.add(v.copyLocation());
		}
		return res;
	}

	/**
	 * version 0.1 draw a simple grid
	 */
	public void drawGrid() {
		setLayout(new GridLayout(getSize_h(), getSize_w()));
		for (int i = 0; i < getSize_h(); i++) {
			for (int j = 0; j < getSize_w(); j++) {
				city_squares[i][j] = new Gridpanel(i, j);
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
		public boolean collector = false, burner = false, waste = false, oneway_road = false, twoway_road = false,
				oneway_road_vert = false, twoway_road_vert = false, oneway_road_east = false, oneway_road_west = false,
				oneway_road_north = false, oneway_road_south = false;

		public Gridpanel(int x, int y) {
			// TODO Auto-generated constructor stub
			this.x = x;
			this.y = y;
			// this.setBorder(BorderFactory.createLineBorder(Color.RED));
			addMouseListener(this);
		}

		public void updateImage() throws IOException {
			if (collector) {
				setIcon(new StretchIcon(ImageIO.read(new File("images/collector_on_road.png")), false));

				return;
			}
			if (burner) {
				return;
			}
			if (waste) {
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
