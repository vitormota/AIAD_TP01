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
	
	public LinkedList<Location> getAgentTrip(Location pos, Location dest) {
        if (env == null)
                return null;
        return env.getAgentTrip(pos, dest);
}

}
