package cityGarbageCollector.agent;

import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plans;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;


@Agent
public class BurnerBDI {

	@Agent
	protected BDIAgent agent;
	
	@Belief
	private Location position;
	
	@Belief
	private int wasteDumped;
	
	@AgentCreated
	public void init() {
		wasteDumped = 0;
		position = new Location(1,5);
		GCollector.getInstance().addBurnerAgent(this);
	}

	@AgentBody
	public void body() {
		
	}
	
	@AgentKilled
	public void killed() {

	}
	
	public void dumpWaste(int quantity) {
		wasteDumped += quantity;		
	}


	/**
	 * 
	 * @return a copy of the agent location
	 */
	public Location getLocation() {
		return position.clone();
	}

	/**
	 * @return the wasteDumped
	 */
	public int getWasteDumped() {
		return wasteDumped;
	}


}
