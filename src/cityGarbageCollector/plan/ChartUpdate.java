package cityGarbageCollector.plan;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.runtime.IPlan;
import cityGarbageCollector.agent.ChartsBDI;

@Plan
public class ChartUpdate {

	@PlanCapability
	protected ChartsBDI chart;

	@PlanAPI
	protected IPlan rplan;

	// -------- constructors --------

	public ChartUpdate() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * The plan body.
	 * 
	 * @throws InterruptedException
	 */
	@PlanBody
	public void body() throws InterruptedException {
		chart.updateChart();
		Thread.sleep((long) (ChartsBDI.SLEEP_MILLIS));
	}
}
