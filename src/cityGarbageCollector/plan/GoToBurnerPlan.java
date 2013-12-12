package cityGarbageCollector.plan;

import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import cityGarbageCollector.agent.CollectorBDI;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;

@Plan
public class GoToBurnerPlan {

	@PlanCapability
	private CollectorBDI collector;
	
	

	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public GoToBurnerPlan() {
		System.out.println("created: " + this);
	}

	// -------- methods --------

	

	/**
	 * The plan body.
	 * @throws InterruptedException 
	 */
	@PlanBody
	public void body() throws InterruptedException {
		System.out.println("GOTOBURNER Plan body!");
		//descola-se para a posição do burner
		collector.updatePosition();
	}
}