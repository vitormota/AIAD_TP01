package cityGarbageCollector;

import cityGarbageCollector.gui.Environment;

/**
 * A position on the city
 * 
 * @author vitor_000
 * 
 */
public class Location {

	public int x;
	public int y;

	public Location(int x, int y) {
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y = y;
	}

	/**
	 * Moves one position Direction of movement: 
	 * /->->->->*
	 * |<-<-<-<-\
	 * *->->->->|
	 */
	public void autoMove() {
		Environment env = GCollector.getInstance().env;
		++x;
		if (x == env.getCityWidth()) {
			x = 0;
			y++;
			if (y == env.getCityHeight()) {
				y = 0;
			}
		}

	}

	public String toString() {
		return "Location: " + x + "," + y + ".";
	}

}
