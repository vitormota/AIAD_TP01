package cityGarbageCollector.plan;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import cityGarbageCollector.Location;
import cityGarbageCollector.agent.CollectorBDI;

@Plan
public class GoTo {

	@PlanCapability
	protected CollectorBDI collector;

	@PlanAPI
	protected IPlan rplan;

	private int x, y;

	// -------- constructors --------

	public GoTo(int x, int y) {
		// TODO Auto-generated constructor stub
		System.out.println("Go to initialized...");
		this.x = x;
		this.y = y;
	}

	// -------- methods --------

	/**
	 * The plan body.
	 * 
	 * @throws InterruptedException
	 */
	@PlanBody
	public void body() throws InterruptedException {
		collector.goToLocation(new Location(x, y));
	}

}