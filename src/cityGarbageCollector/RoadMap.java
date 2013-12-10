package cityGarbageCollector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import com.sun.corba.se.impl.orbutil.graph.Node;

/**
 * Map of roads, graph and operations
 * 
 * @author vitor
 * 
 */
public class RoadMap {

	/**
	 * Activates verbose messages
	 * Default = false
	 */
	public static boolean verbose = true;

	public static final char COMMENT = '#';
	public static final char SIZE = 's';

	private boolean error_state = false;

	/**
	 * List of pointers to vertices that are turns.
	 * Not copies
	 */
	private List<Vertex> turns;

	private List<Vertex> vertices;
	public int sizeX;
	public int sizeY;

	public RoadMap(String file) throws FileNotFoundException {
		turns = new ArrayList<>();
		vertices = new ArrayList<>();
		if (file != null)
			loadMapFromFile(file);
	}

	private void loadMapFromFile(String file) throws FileNotFoundException {
		// TODO Auto-generated method stub
		File f = new File(file);
		Scanner read = new Scanner(f);
		int roads = 0, lines = 0;
		while (read.hasNextLine()) {
			if (parseLine(read.nextLine(), lines)) {
				roads++;
			}
			lines++;
		}
		if (verbose)
			System.out.println("Read " + roads + " roads.");
		read.close();
	}

	public Road[] getRoads() {
		List<Edge> edges = getEdges();
		Road[] res = new Road[edges.size()];
		Direction dir;
		boolean turn;
		Location loc;
		Edge e;
		for (int i = 0; i < res.length; i++) {
			e = edges.get(i);
			turn = isTurn(getVertexByLocation(edges.get(i).getSourceLocation()));
			if (e.getSourceLocation().x < e.getDestLocation().x) {
				dir = Direction.EAST;
			} else if (e.getSourceLocation().x > e.getDestLocation().x) {
				dir = Direction.WEST;
			} else if (e.getSourceLocation().y > e.getDestLocation().y) {
				dir = Direction.NORTH;
			} else {
				dir = Direction.SOUTH;
			}
			loc = edges.get(i).getSourceLocation();
			res[i] = new Road(dir, loc, turn);
		}
		return res;
	}

	private List<Edge> getEdges() {
		// TODO Auto-generated method stub
		List<Edge> edges = new ArrayList<>();
		for (Vertex v : vertices) {
			edges.addAll(v.getEdges());
		}
		return edges;
	}

	private boolean parseLine(String nextLine, int line) {
		// TODO Auto-generated method stub
		if (nextLine.length() == 0 || nextLine.toCharArray()[0] == COMMENT)
			return false;
		if (nextLine.toCharArray()[0] == SIZE) {
			try {
				String[] size_data = nextLine.split(" ");
				size_data = size_data[1].split("x");
				sizeX = Integer.parseInt(size_data[0]);
				sizeY = Integer.parseInt(size_data[1]);
			} catch (NumberFormatException ex) {
				if (verbose)
					System.err.println("CRITICAL: Bad size for map.");
				error_state = true;
				System.exit(1);
			}
			return false;
		}
		int x, x1, y, y1, way;
		String[] data = nextLine.split(" ");
		String[] from_s = data[0].split(",");
		String[] to_s = data[1].split(",");
		try {
			x = Integer.parseInt(from_s[0]);
			y = Integer.parseInt(from_s[1]);
			x1 = Integer.parseInt(to_s[0]);
			y1 = Integer.parseInt(to_s[1]);
			way = Integer.parseInt(data[2]);
			if (way != 0 && way != 1)
				throw new NumberFormatException();
		} catch (NumberFormatException ex) {
			if (verbose) {
				System.out.println("Invalid road at line " + line + ".");
			}
			return false;
		}
		Location loc = new Location(x, y);
		Location loc1 = new Location(x1, y1);
		Vertex v = getVertexByLocation(loc);
		if (v == null)
			v = new Vertex(loc);
		Vertex v1 = getVertexByLocation(loc1);
		if (v1 == null)
			v1 = new Vertex(loc1);
		addVertice(v);
		addVertice(v1);
		turns.add(v1);
		addMidwayVertices(loc, loc1);
		return true;
	}

	/**
	 * Check if vertex is a turn, i.e. it was parsed
	 * from input file as a destination
	 * 
	 * @param v
	 *            the vertex to check for
	 * @return true if v is a turn
	 */
	public boolean isTurn(Vertex v) {
		return turns.contains(v);
	}

	/**
	 * Adds midway vertices from two locations
	 * 
	 * THIS ASSUMES THAT NO DIAGONALS ARE ENTERED
	 * or it won't work...
	 * 
	 * @param source
	 *            vertex
	 * @param dest
	 *            vertex
	 */
	private void addMidwayVertices(Location source, Location dest) {
		// TODO Auto-generated method stub
		Vertex tmp;
		Location loc;
		while (!source.equals(dest)) {
			loc = source.clone();
			if (source.x == dest.x) {
				if (source.y < dest.y)
					source.y++;
				else
					source.y--;

			} else {
				if (source.x < dest.x)
					source.x++;
				else
					source.x--;
			}
			tmp = getVertexByLocation(new Location(source.x, source.y));
			if (tmp == null) {
				// it is a new vertex
				tmp = new Vertex(new Location(source.x, source.y));
			}
			Vertex from = getVertexByLocation(loc);
			from.addEdge(new Edge(from, tmp));
			addVertice(tmp);
		}
	}

	/*
	 * ##############################################################
	 * # #
	 * # Graph search methods #
	 * # #
	 * ##############################################################
	 */

	/**
	 * Depth-first search algorithm to compute the travel
	 * sequence for collectors
	 * 
	 * @return a linked list containing to sequence of edges to be traveled
	 */
	public LinkedList<Vertex> getAgentCircuit(Vertex start) {
		LinkedList<Vertex> res = new LinkedList<>();
		Stack<Edge> s = new Stack<>();
		res.addLast(start);
		boolean found;
		do {
			if (start.getEdges().size() == 0)
				return res;
			if (start.getEdges().size() == 1) {
				// if one -> relax that
				res.addLast(start.getEdges().get(0).getTo());
			} else {
				for (Edge e : start.getEdges()) {
					if (!res.contains(e.getTo())) {
						s.push(e);
					}
				}
				if (s.size() != 0) {
					if (start.getEdges().contains(s.peek())) {
						res.addLast(s.pop().getTo());
					} else {
						Vertex v = searchAhead(start, res);
						if (v == null) {
							return res;
						}
						res.addLast(v);
					}
				}
			}
			start = res.getLast();
		} while (!allVisited(res));
		return res;
	}

	private Map<Vertex, Boolean> vis = new HashMap<Vertex, Boolean>();

	private Map<Vertex, Vertex> prev = new HashMap<Vertex, Vertex>();

	/**
	 * Returns a list of vertices that specify the route to travel from start to
	 * dest
	 * 
	 * @param source
	 *            starting point
	 * @param destination
	 *            destination
	 * @return a list of vertices
	 */
	public List<Vertex> getDirections(Location start, Location dest) {
		List<Vertex> directions = new LinkedList<>();
		Queue<Vertex> q = new LinkedList<Vertex>();
		Vertex finish = getVertexByLocation(dest);
		Vertex current = getVertexByLocation(start);
		q.add(current);
		vis.put(current, true);
		while (!q.isEmpty()) {
			current = (Vertex) q.remove();
			if (current.equals(finish)) {
				break;
			} else {
				for (Vertex node : current.getOutNodes()) {
					if (!vis.containsKey(node)) {
						q.add(node);
						vis.put(node, true);
						prev.put(node, current);
					}
				}
			}
		}
		if (!current.equals(finish)) {
			System.out.println("can't reach destination");
		}
		for (Vertex node = finish; node != null; node = prev.get(node)) {
			directions.add(node);
		}
		// directions.reverse();
		List<Vertex> res = new LinkedList<>();
		for (int i = directions.size() - 1; i >= 0; i--) {
			res.add(directions.get(i));
		}

		return res;
	}

	private boolean allVisited(LinkedList<Vertex> list) {
		// TODO Auto-generated method stub
		return list.containsAll(vertices);
	}

	private Vertex searchAhead(Vertex start, LinkedList<Vertex> list) {
		// TODO Auto-generated method stub
		List<Vertex> lv = new ArrayList<>();
		lv.add(start);
		Stack<Edge> s = new Stack<>();
		Edge e;
		for (Edge edge : start.getEdges()) {
			s.push(edge);
			while (s.size() != 0) {
				e = s.pop();
				if (!list.contains(e.getTo())) {
					return edge.getTo();
				} else {
					if (!lv.contains(e.getTo())) {
						// prevent loop
						lv.add(e.getTo());
						addToStack(e.getTo().getEdges(), s);
					}

				}
			}
		}
		return null;
	}

	private void addToStack(List<Edge> edges, Stack<Edge> s) {
		// TODO Auto-generated method stub
		for (int i = 0; i < edges.size(); i++) {
			s.push(edges.get(i));
		}
	}

	public Vertex getVertexByLocation(Location loc) {
		for (Vertex v : vertices) {
			if (v.equals(new Vertex(loc))) {
				return v;
			}
		}
		return null;
	}

	/**
	 * Adds one vertex to roadmap only if it isn't already present.
	 * Criteria is the vertex location
	 * 
	 * @param vert
	 *            the vertex to add
	 */
	private boolean addVertice(Vertex vert) {
		for (int i = 0; i < vertices.size(); i++) {
			if (vert.equals(vertices.get(i))) {
				if (verbose) {
					System.out.println("Vertex already present, not added --> " + vert + ".");
				}
				return false;
			}
		}
		vertices.add(vert);
		if (verbose) {
			System.out.println("New vertex added --> " + vert + ".");
		}
		return true;
	}

	public enum Direction {
		NORTH, SOUTH, EAST, WEST
	};

	public class Road {
		public boolean turn;
		public Direction direction;
		public Location location;

		public Road(Direction dir, Location loc, boolean turn) {
			// TODO Auto-generated constructor stub
			location = loc.clone();
			direction = dir;
			this.turn = turn;
		}
	}
}
