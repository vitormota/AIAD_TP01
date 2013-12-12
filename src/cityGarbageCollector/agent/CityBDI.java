package cityGarbageCollector.agent;

import java.io.FileNotFoundException;
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
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;
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
	protected Location[] collector_locations;
	
	@Belief
	protected Location[] container_locations;
	
	@Belief
	protected Location[] burner_locations;

	@Belief
	protected Environment env;

	@Belief
	protected boolean pause = false;
	
	@Belief
	public static final long SLEEP_MILLIS = 100;

	/**
	 * The agent body
	 */
	@AgentBody
	public void body() {
		try {
			env = new Environment(agent.getExternalAccess());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GCollector.getInstance().setEnv(env);
		agent.dispatchTopLevelGoal(new PerformMapUpdate()).get();
	}

	public void updateMap() {
		container_locations = GCollector.getInstance().getContainerlocations();
		burner_locations = GCollector.getInstance().getBurnerlocations();
		
		
		// get Agent new Locations
		Location[] collectorLocations_new = GCollector.getInstance().getCollectorlocations();
		// clear previous location icons
		if (collector_locations != null) {
			for (int i = 0; i < collector_locations.length; i++) {
				if (collectorLocations_new[i].equals(collector_locations[i]))
					// means location of agent was not updated since last time
					// this will fix the blinking agent bug
					continue;
				env.getCitySpacebyLocation(collector_locations[i]).collector = false;
				try {
					env.getCitySpacebyLocation(collector_locations[i]).updateImage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (GCollector.verbose) {
						System.err.println("ERROR: The specified image was not found!");
					}
				}
			}
		}

		// draw collector agents on map
		for (int i = 0; i < collectorLocations_new.length; i++) {
			env.getCitySpacebyLocation(collectorLocations_new[i]).collector = true;
			try {
				env.getCitySpacebyLocation(collectorLocations_new[i]).updateImage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (GCollector.verbose) {
					System.err.println("ERROR: The specified image was not found!");
				}
			}
		}
		
		// draw container agents on map
		for (int i = 0; i < container_locations.length; i++) {
			env.getCitySpacebyLocation(container_locations[i]).container = true;
			try {
				env.getCitySpacebyLocation(container_locations[i]).updateImage();
			} catch (IOException e) {
				e.printStackTrace();
				if (GCollector.verbose) {
					System.err.println("ERROR: The specified image was not found!");
				}
			}
		}
		
		// draw burner agents on map
		for (int i = 0; i < burner_locations.length; i++) {
			env.getCitySpacebyLocation(burner_locations[i]).burner = true;
			try {
				env.getCitySpacebyLocation(burner_locations[i]).updateImage();
			} catch (IOException e) {
				e.printStackTrace();
				if (GCollector.verbose) {
					System.err.println("ERROR: The specified image was not found!");
				}
			}
		}
		
		collector_locations = new Location[collectorLocations_new.length];
		System.arraycopy(collectorLocations_new, 0, collector_locations, 0, collector_locations.length);
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
