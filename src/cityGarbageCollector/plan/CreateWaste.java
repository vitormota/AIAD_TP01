package cityGarbageCollector.plan;

import cityGarbageCollector.GCollector;
import cityGarbageCollector.agent.CollectorBDI;
import cityGarbageCollector.agent.ContainerBDI;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;

@Plan
public class CreateWaste {

	@PlanCapability
	private ContainerBDI container;

	// -------- constructors --------
	
	/**
	 * Create a new plan.
	 */
	public CreateWaste() {

	}

	// -------- methods --------

	/**
	 * The plan body.
	 * @throws InterruptedException 
	 */
	@PlanBody
	public void body() throws InterruptedException{
		container.incrementWaste();
		Thread.sleep((long) (CollectorBDI.SLEEP_MILLIS / GCollector.getInstance().speed()));
	}

}
