package cityGarbageCollector;

import java.util.ArrayList;
import cityGarbageCollector.agent.ContainerBDI;
import cityGarbageCollector.gui.Environment;

/**
 * Singleton superinformed class
 * 
 */
public class GContainer {

	/**
	 * singleton instance
	 */
	private static GContainer instance = null;
	public static boolean verbose = true;

	private Environment env = null;

	private ArrayList<ContainerBDI> agents;

	protected GContainer() {
		agents = new ArrayList<>();
	}

	public static GContainer getInstance() {
		if (instance == null)
			instance = new GContainer();
		return instance;
	}

	public void addAgent(ContainerBDI agent) {
		agents.add(agent);
	}

	public Location[] getAgentlocations() {
		Location[] res = new Location[agents.size()];
		int i = 0;
		for (ContainerBDI cl : agents) {
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
