package cityGarbageCollector;

import java.util.ArrayList;

import cityGarbageCollector.agent.CollectorBDI;
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

	private ArrayList<CollectorBDI> agents;

	protected GCollector() {
		agents = new ArrayList<>();
	}

	public static GCollector getInstance() {
		if (instance == null)
			instance = new GCollector();
		return instance;
	}

	public void addAgent(CollectorBDI agent) {
		agents.add(agent);
	}

	public Location[] getAgentlocations() {
		Location[] res = new Location[agents.size()];
		int i = 0;
		for (CollectorBDI cl : agents) {
			res[i++] = cl.getLocation();
		}
		return res;
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

}
