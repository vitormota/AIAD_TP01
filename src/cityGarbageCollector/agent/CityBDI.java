package cityGarbageCollector.agent;

import cityGarbageCollector.GCollector;
import cityGarbageCollector.gui.Environment;
import jadex.bdiv3.BDIAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 * Agent representing the world
 * 
 * @author vitor_000
 *
 */
@Agent
public class CityBDI {

	@Agent
	protected BDIAgent agent;
	
	/**
	 * The agent body
	 */
	@AgentBody
	public void body(){
		Environment env  = new Environment(agent.getExternalAccess());
		GCollector.getInstance().env = env;
	}
}
