package cityGarbageCollector.agent;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Goal.ExcludeMode;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import cityGarbageCollector.plan.PickUpWastePlan;
import cityGarbageCollector.plan.Wander;

@Agent
@Plans(
{
	@Plan(trigger = @Trigger(goals = CollectorBDI.PerformPatrol.class), priority=2, body = @Body(Wander.class)),
	@Plan(trigger = @Trigger(goals = CollectorBDI.checkContainer.class), priority=1, body = @Body(PickUpWastePlan.class))
})
public class CollectorBDI {

	@Agent
	protected BDIAgent agent;

	@Belief
	private int capacity;
	@Belief
	private int actualWasteQuantity;
	@Belief
	private Location position;
	@Belief
	private boolean pause = false;

	public int getRemainingCapacity() {
		return capacity-actualWasteQuantity;
	}
	
	@AgentCreated
	public void init() {
		position = new Location(0, 0);
		capacity=50;
		GCollector.getInstance().addAgent(this);
		actualWasteQuantity=0;
	}

	@AgentBody
	public void body() {
		agent.dispatchTopLevelGoal(new PerformPatrol());
		agent.dispatchTopLevelGoal(new checkContainer());
		System.out.println("agentbody");
	}

	@AgentKilled
	public void killed()
	{

	}

	public void updatePosition() throws InterruptedException {
		this.position.autoMove();
		Thread.sleep(1000);
		// DEBUG
		//System.out.println(position);
	}
	
	public void pickWaste(int quantity) {
		actualWasteQuantity+=quantity;
	}

	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class checkContainer {
		
	}
	
	@Goal
	public class PickUpWaste {
		
	}
	
	
	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class PerformPatrol {
		/**
		 * Suspend the goal when on pause.
		 */
		@GoalContextCondition(rawevents = "pause")
		public boolean checkContext()
		{
			return !pause;
		}
	}
	

	/**
	 * 
	 * @return a copy of the agent location
	 */
	public Location getLocation() {
		// TODO Auto-generated method stub
		return position.clone();
	}

}