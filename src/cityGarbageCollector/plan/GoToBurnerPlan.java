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
		//System.out.println("created: " + this);
	}

	// -------- methods --------



	/**
	 * The plan body.
	 * @throws InterruptedException 
	 */
	@PlanBody
	public void body() throws InterruptedException {
		System.out.println("GOTOBURNER Plan body!");
		while (collector.pause) {
			Thread.sleep(1000);
		}

		if(collector.aux==false) {
			//descola-se para a posição do burner
			Location loc = collector.getNearestBurner();
			collector.goToLocation(loc);
			collector.aux=true;
		}

		collector.updatePosition();
		Thread.sleep((long) (CollectorBDI.SLEEP_MILLIS / GCollector.getInstance().speed()));
	}
}