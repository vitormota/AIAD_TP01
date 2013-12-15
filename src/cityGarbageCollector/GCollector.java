package cityGarbageCollector;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cityGarbageCollector.RoadMap.Road_Type;
import cityGarbageCollector.agent.BurnerBDI;
import cityGarbageCollector.agent.CityBDI;
import cityGarbageCollector.agent.CollectorBDI;
import cityGarbageCollector.agent.CollectorBDI.Trash_Type;
import cityGarbageCollector.agent.ContainerBDI;
import cityGarbageCollector.gui.City.Gridpanel;
import cityGarbageCollector.gui.Environment;

/**
 * Singleton superinformed class
 * 
 * @author vitor_000
 * 
 */
public class GCollector {

	// ================================================================================
	// Public
	// ================================================================================

	public static enum Agents_models {
		Collector, Container, Burner
	}

	public static boolean verbose = true;

	public static enum SPEED {
		Slow(0.5f), Normal(1.0f), Fast(2.0f), Super(3.0f);

		public float speed;

		private SPEED(float value) {
			// TODO Auto-generated constructor stub
			speed = value;
		}
	};

	// ================================================================================
	// Private declarations
	// ================================================================================

	private Environment env = null;
	private ArrayList<CollectorBDI> collector_agents;
	private ArrayList<ContainerBDI> container_agents;
	private ArrayList<BurnerBDI> burner_agents;
	private float speed = SPEED.Normal.speed;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private static ThreadSuspendable sus;
	private static IExternalAccess pl;
	private static IComponentManagementService cms;
	/**
	 * singleton instance
	 */
	private static GCollector instance = null;

	// ================================================================================
	// Constructors
	// ================================================================================

	protected GCollector() {
		collector_agents = new ArrayList<>();
		container_agents = new ArrayList<>();
		burner_agents = new ArrayList<>();
	}

	// ================================================================================
	// Operations
	// ================================================================================

	public void launchAgent(String classPath, CreationInfo cInfo) {
		CityBDI.city.deployAgent(classPath, cInfo);
	}

	public static GCollector getInstance() {
		if (instance == null)
			instance = new GCollector();
		return instance;
	}

	public String getCollectorTag(Location loc) {
		String res = "<html><font color='";
		int cap = 0, quant = 0;
		Trash_Type t = Trash_Type.Common;
		for (CollectorBDI c : collector_agents) {
			if (c.getLocation().equals(loc)) {
				t = c.type;
				cap = c.getCapacity();
				quant = c.getActualWasteQuantity();
				break;
			}
		}
		switch (t) {
		case Common:
			res += "green";
			break;
		case Metal:
			res += "white";
			break;
		case Paper:
			res += "yellow";
			break;
		case Plastic:
			res += "#00FFFF";
			break;
		default:
			break;
		}
		res += "'><b>" + quant + "/" + cap + "</b></font></html>";
		return res;
	}

	public boolean isRoadOnLocation(Location loc) {
		Gridpanel gp = env.getCitySpacebyLocation(loc);
		return gp == null ? false : gp.hasRoad();
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
			if (c.getLocation().equals(loc))
				return c;
		}
		return null;
	}

	public BurnerBDI getBurnerByLocation(Location loc) {
		for (BurnerBDI b : burner_agents) {
			if (b.getLocation().equals(loc))
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
		for (CollectorBDI c : collector_agents) {
			c.togglePause();
		}
	}

	public boolean getPauseState() {
		if (env == null) {
			return false;
		}
		return env.getPauseState();
	}

	/**
	 * [0] - containers
	 * [1] - collectors
	 * [2] - burners
	 * [3] - all
	 * 
	 * @return an array with trash count on all places
	 */
	public int[] getTrashCount() {
		int sum[] = { 0, 0, 0, 0 };
		for (ContainerBDI b : container_agents) {
			sum[0] += b.getWasteQuantity();
		}
		for (CollectorBDI c : collector_agents) {
			sum[1] += c.getActualWasteQuantity();
		}
		for (BurnerBDI b : burner_agents) {
			sum[2] += b.getWasteDumped();
		}
		sum[3] = sum[0] + sum[1] + sum[2];
		return sum;
	}

	public void modifyMap(Road_Type type, int x, int y) {
		// TODO Auto-generated method stub
		env.modifyMap(type, x, y);
	}

}
