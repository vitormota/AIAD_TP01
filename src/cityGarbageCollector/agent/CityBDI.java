package cityGarbageCollector.agent;

import java.io.IOException;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Goal.ExcludeMode;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import cityGarbageCollector.gui.Environment;
import cityGarbageCollector.plan.MapUpdate;

/**
 * Agent representing the world
 * 
 * @author vitor_000
 * 
 */
@Agent
@Plans({ @Plan(trigger = @Trigger(goals = CityBDI.PerformMapUpdate.class), body = @Body(MapUpdate.class)) })
public class CityBDI {

	@Agent
	protected BDIAgent agent;

	@Belief
	protected Location[] agent_locations;

	@Belief
	protected Environment env;

	@Belief
	protected boolean pause = false;

	/**
	 * The agent body
	 */
	@AgentBody
	public void body() {
		env = new Environment(agent.getExternalAccess());
		GCollector.getInstance().setEnv(env);
		agent.dispatchTopLevelGoal(new PerformMapUpdate()).get();
	}

	public void updateMap() {
		// get Agent new Locations
		Location[] agentLocations_new = GCollector.getInstance().getAgentlocations();
		// clear previous location icons
		if (agent_locations != null) {
			for (int i = 0; i < agent_locations.length; i++) {
				if (agentLocations_new[i].equals(agent_locations[i]))
					// means location of agent was not updated since last time
					// this will fix the blinking agent bug
					continue;
				env.getCitySpacebyLocation(agent_locations[i]).collector = false;
				try {
					env.getCitySpacebyLocation(agent_locations[i]).updateImage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (GCollector.verbose) {
						System.err.println("ERROR: The specified image was not found!");
					}
				}
			}
		}

		// draw agents on map
		for (int i = 0; i < agentLocations_new.length; i++) {
			env.getCitySpacebyLocation(agentLocations_new[i]).collector = true;
			try {
				env.getCitySpacebyLocation(agentLocations_new[i]).updateImage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (GCollector.verbose) {
					System.err.println("ERROR: The specified image was not found!");
				}
			}
		}
		agent_locations = new Location[agentLocations_new.length];
		System.arraycopy(agentLocations_new, 0, agent_locations, 0, agent_locations.length);
	}

	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class PerformMapUpdate {

		/**
		 * Suspend the goal when on pause.
		 */
		@GoalContextCondition(rawevents = "pause")
		public boolean checkContext() {
			return !pause;
		}

	}
}
