package cityGarbageCollector.agent;

import cityGarbageCollector.GContainer;
import cityGarbageCollector.Location;
import cityGarbageCollector.plan.CreateWaste;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.annotation.Goal.ExcludeMode;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;


@Agent
@Plans({ 
	@Plan(trigger = @Trigger(goals = ContainerBDI.PerformCreateWaste.class), body = @Body(CreateWaste.class)) })
public class ContainerBDI {

	@Agent
	protected BDIAgent agent;

	private int cont = 0;
	
	@Belief
	private Location position;
	@Belief
	private boolean pause = false;
	@Belief
	private int wasteQuantity=0;

	public int getWasteQuantity() {
		return wasteQuantity;
	}

	@AgentCreated
	public void init() {
		position = new Location(2,1);
		GContainer.getInstance().addAgent(this);
	}

	@AgentBody
	public void body() {
		agent.dispatchTopLevelGoal(new PerformCreateWaste()).get();
	}
	
	@AgentKilled
	public void killed() {

	}

	public void incrementWaste() throws InterruptedException {
		cont++;
		if(cont==50000) {
			wasteQuantity+=1;
			cont=0;
		}
		//System.out.println("Waste quantity: "+(int)wasteQuantity);
	}


	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class PerformCreateWaste {

		/**
		 * Suspend the goal when on pause.
		 */
		@GoalContextCondition(rawevents = "pause")
		public boolean checkContext() {
			return !pause;
		}

	}

	/**
	 * 
	 * @return a copy of the agent location
	 */
	public Location getLocation() {
		return position.clone();
	}

	public void decrementWaste(int quantity) {
		wasteQuantity-=quantity;
	}
}
