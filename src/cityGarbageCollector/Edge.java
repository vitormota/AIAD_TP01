package cityGarbageCollector;

public class Edge {

	public static final char HORIZONTAL = 'H';
	public static final char VERTICAL = 'V';
	
	private Vertex from;
	private Vertex to;
	
	public Edge(Vertex from, Vertex to) {
		// TODO Auto-generated constructor stub
		this.from = from;
		this.to = to;
	}
	
//	public Edge(Vertex to){
//		this.to = to.clone();
//	}
	
	public char getDirection(){
		return (from.getX() == to.getX() ? VERTICAL : HORIZONTAL);
	}
	
	public boolean equal(Edge e){
		return (from.equals(e.from) && to.equals(e.to));
	}
	
	public String toString(){
		return "("+from+")-("+to+")";
	}

	public Location getSourceLocation() {
		// TODO Auto-generated method stub
		return from.copyLocation();
	}
}
