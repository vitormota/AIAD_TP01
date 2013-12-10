package cityGarbageCollector.plan;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.runtime.IPlan;
import cityGarbageCollector.GCollector;
import cityGarbageCollector.agent.CollectorBDI;

@Plan
public class Wander {

	@PlanCapability
	protected CollectorBDI collector;

	@PlanAPI
	protected IPlan rplan;

	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public Wander() {
		// System.out.println("created: " + this);
	}

	// -------- methods --------

	/**
	 * The plan body.
	 * 
	 * @throws InterruptedException
	 */
	@PlanBody
	public void body() throws InterruptedException {
		// System.out.println("at wander planbody");
		collector.updatePosition();
		Thread.sleep((long) (CollectorBDI.SLEEP_MILLIS / GCollector.getInstance().speed()));
	}

}
