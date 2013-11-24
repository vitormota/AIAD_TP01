package cityGarbageCollector.agent;

import java.util.LinkedList;

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
import cityGarbageCollector.Vertex;
import cityGarbageCollector.plan.Wander;

@Agent
@Plans({ @Plan(trigger = @Trigger(goals = CollectorBDI.PerformPatrol.class), body = @Body(Wander.class)) })
public class CollectorBDI {

	@Agent
	protected BDIAgent agent;

	@Belief
	private Location position;
	@Belief
	private boolean pause = false;
	private LinkedList<Location> steps;

	@AgentCreated
	public void init() {
		position = new Location(0, 0);
		steps = new LinkedList<>();
		GCollector.getInstance().addAgent(this);
	}

	@AgentBody
	public void body() {
		agent.dispatchTopLevelGoal(new PerformPatrol()).get();
	}

	@AgentKilled
	public void killed() {

	}

	public void updatePosition() throws InterruptedException {
		// this.position.autoMove();
		if (this.steps != null && this.steps.size() == 0) {
			// ask for new route
			steps = GCollector.getInstance().getAgentTrip(position);
		}
		if(steps!=null){
			this.position = steps.removeFirst();
		}else{
			this.position.autoMove();
		}
		Thread.sleep(500);
		// DEBUG
		System.out.println(position);
	}

	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class PerformPatrol {

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
		// TODO Auto-generated method stub
		return position.clone();
	}
}
