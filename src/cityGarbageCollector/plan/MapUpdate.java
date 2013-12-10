package cityGarbageCollector.plan;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import cityGarbageCollector.GCollector;
import cityGarbageCollector.agent.CityBDI;

@Plan
public class MapUpdate {

	@PlanCapability
	protected CityBDI city;

	@PlanAPI
	protected IPlan rplan;

	// -------- constructors --------

	public MapUpdate() {
		// TODO Auto-generated constructor stub
	}

	// -------- methods --------

	/**
	 * The plan body.
	 * @throws InterruptedException 
	 */
	@PlanBody
	public void body() throws InterruptedException{
		city.updateMap();
		Thread.sleep((long) (CityBDI.SLEEP_MILLIS / GCollector.getInstance().speed()));
	}
	
}
