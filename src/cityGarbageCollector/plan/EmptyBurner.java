package cityGarbageCollector.plan;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import cityGarbageCollector.GCollector;
import cityGarbageCollector.agent.BurnerBDI;
import cityGarbageCollector.agent.ContainerBDI;

@Plan
public class EmptyBurner {

	@PlanCapability
	private BurnerBDI burner;

	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public EmptyBurner() {
		// TODO Auto-generated constructor stub
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
			Thread.sleep(1000);
		}
		burner.burnWaste();
		Thread.sleep((long) ((ContainerBDI.SLEEP_MILLIS * (Math.random()) + 0.5) / GCollector.getInstance().speed()));
	}

}
