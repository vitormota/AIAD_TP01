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
		Environment env = GCollector.getInstance().getEnv();
		if (env == null) {
			if (GCollector.verbose) {
				// VERBOSE
				System.out.println("World not defined, agent will wander endlessly...");
			}
			// Random move
			x += (Math.round(Math.random()));
			y += (Math.round(Math.random()));
			return;
		}

		++x;
		if (x >= env.getCityWidth()) {
			x = 0;
			y++;
			if (y >= env.getCityHeight()) {
				y = 0;
			}
		}

	}

	public boolean equals(Location loc) {
		return x == loc.x && y == loc.y ? true : false;
	}

	@Override
	public Location clone() {
		return new Location(this.x, this.y);
	}

	public String toString() {
		return x + "," + y;
	}

}
