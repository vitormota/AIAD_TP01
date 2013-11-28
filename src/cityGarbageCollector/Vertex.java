package cityGarbageCollector;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
	private List<Edge> edges;
	/**
	 * Defines a position on the grid
	 * Also serves as vertex id
	 */
	private Location position;

	public Vertex(Location loc) {
		// TODO Auto-generated constructor stub
		this.position = loc.clone();
		this.edges = new ArrayList<>();
	}

	public Vertex clone() {
		Vertex res = new Vertex(position);
		//System.arraycopy(getEdges().toArray(), 0, res.getEdges().toArray(), 0, getEdges().size());
		res.getEdges().addAll(getEdges());
		return res;
	}

	public boolean equals(Vertex vert) {
		return vert.position.equals(this.position);
	}

	public String toString() {
		return position.toString();
	}

	/**
	 * Adds edge e if not already present
	 * 
	 * @param e
	 *            the edge to add
	 * @return true if added
	 */
	public boolean addEdge(Edge e) {
		for (int i = 0; i < getEdges().size(); i++) {
			if (getEdges().get(i).equal(e)) {
				if (RoadMap.verbose) {
					System.out.println("Edge already present --> " + e);
				}
				return false;
			}
		}
		edges.add(e);
		return true;
	}

	public int getX() {
		// TODO Auto-generated method stub
		return this.position.x;
	}

	public int getY() {
		return this.position.y;
	}

	/**
	 * @return the edges
	 */
	public List<Edge> getEdges() {
		return edges;
	}

	/**
	 * Get a copy of this vertex location
	 * 
	 * @return a clone of the location
	 */
	public Location copyLocation() {
		// TODO Auto-generated method stub
		return position.clone();
	}

}
