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
	 * 
	 * @throws InterruptedException
	 */
	@PlanBody
	public void body() throws InterruptedException {
		while (GCollector.getInstance().getPauseState()) {
			Thread.sleep(ContainerBDI.SLEEP_MILLIS);
		}
		container.incrementWaste();
		Thread.sleep((long) ((ContainerBDI.SLEEP_MILLIS * (Math.random()) + 0.2) / GCollector.getInstance().speed()));
	}

}
