package cityGarbageCollector;

import java.util.ArrayList;
import java.util.LinkedList;

import cityGarbageCollector.agent.CollectorBDI;
import cityGarbageCollector.agent.ContainerBDI;
import cityGarbageCollector.agent.BurnerBDI;
import cityGarbageCollector.gui.Environment;

/**
 * Singleton superinformed class
 * 
 * @author vitor_000
 * 
 */
public class GCollector {

	/**
	 * singleton instance
	 */
	private static GCollector instance = null;
	public static boolean verbose = true;

	private float speed = SPEED.Normal.speed;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

	public static enum SPEED {
		Slow(0.5f), Normal(1.0f), Fast(2.0f), Super(3.0f);

		public float speed;

		private SPEED(float value) {
			// TODO Auto-generated constructor stub
			speed = value;
		}
	};

	private Environment env = null;

	private ArrayList<CollectorBDI> collector_agents;
	private ArrayList<ContainerBDI> container_agents;
	private ArrayList<BurnerBDI> burner_agents;
	

	protected GCollector() {
		collector_agents = new ArrayList<>();
		container_agents = new ArrayList<>();
		burner_agents = new ArrayList<>();
	}

	public static GCollector getInstance() {
		if (instance == null)
			instance = new GCollector();
		return instance;
	}
	
	public boolean isRoadOnLocation(Location loc){
		return env.getCitySpacebyLocation(loc).hasRoad();
	}

	public void addCollectorAgent(CollectorBDI agent) {
		collector_agents.add(agent);
	}
	
	public void addContainerAgent(ContainerBDI agent) {
		container_agents.add(agent);
	}
	
	public void addBurnerAgent(BurnerBDI burner) {
		burner_agents.add(burner);
	}

	public Location[] getCollectorlocations() {
		Location[] res = new Location[collector_agents.size()];
		int i = 0;
		for (CollectorBDI cl : collector_agents) {
			res[i++] = cl.getLocation();
		}
		return res;
	}

	public Location[] getContainerlocations() {
		Location[] res = new Location[container_agents.size()];
		int i = 0;
		for (ContainerBDI cl : container_agents) {
			res[i++] = cl.getLocation();
		}
		return res;
	}

public void setSpeed(SPEED s) {
		try {
			lock.writeLock().lock();
			speed = s.speed;
			// JOptionPane.showMessageDialog(null, "Changed " + speed);
		} finally {
			lock.writeLock().unlock();
		}

	}

	public float speed() {
		try {
			lock.readLock().lock();
			return speed;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	
	
	public Location[] getBurnerlocations() {
		Location[] res = new Location[burner_agents.size()];
		int i = 0;
		for (BurnerBDI bl : burner_agents) {
			res[i++] = bl.getLocation();
		}
		return res;
	}
	
	public ContainerBDI getContainerByLocation(Location loc) {
		for (ContainerBDI c : container_agents) {
			if(c.getLocation().equals(loc))
				return c;
		}
		return null;
	}
	
	public BurnerBDI getBurnerByLocation(Location loc) {
		for (BurnerBDI b : burner_agents) {
			if(b.getLocation().equals(loc))
				return b;
		}
		return null;
	}

	/**
	 * Only permitted if current environment is null
	 * 
	 * @param env
	 *            the environment to set
	 */
	public void setEnv(Environment env) {
		if (this.env == null)
			this.env = env;
	}

	public Environment getEnv() {
		return this.env;
	}

	public LinkedList<Location> getAgentTrip(Location pos) {
		if (env == null)
			return null;
		return env.getAgentTrip(pos);
	}

	public LinkedList<Location> getAgentTrip(Location pos, Location dest) {
		if (env == null)
			return null;
		return env.getAgentTrip(pos, dest);
	}

	public void toggleAgentPause() {
		// TODO Auto-generated method stub
		for(CollectorBDI c : agents){
			c.togglePause();
		}
	}


	public void modifyMap(Road_Type type, int x, int y) {
		// TODO Auto-generated method stub
		env.modifyMap(type, x,y);
	}

}
