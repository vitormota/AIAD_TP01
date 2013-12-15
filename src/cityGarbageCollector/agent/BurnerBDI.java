package cityGarbageCollector.agent;

import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import cityGarbageCollector.plan.CreateWaste;
import cityGarbageCollector.plan.EmptyBurner;
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
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

@Agent
@Plans({ @Plan(trigger = @Trigger(goals = BurnerBDI.DeleteWaste.class), body = @Body(EmptyBurner.class)) })
@Arguments({ @Argument(name = "Location", clazz = Location.class),
		@Argument(name = "Type", clazz = CollectorBDI.Trash_Type.class, defaultvalue = "Common") })
public class BurnerBDI {

	public static final String CLASS_PATH = "cityGarbageCollector/agent/BurnerBDI.class";

	@Agent
	protected BDIAgent agent;

	@Belief
	private Location position;

	@Belief
	private int wasteDumped;

	@AgentCreated
	public void init() {
		wasteDumped = 0;
		position = (Location) agent.getArgument("Location");
		if (position == null) {
			position = new Location(1, 5);
		}
		GCollector.getInstance().addBurnerAgent(this);
	}

	@AgentBody
	public void body() {
		agent.dispatchTopLevelGoal(new DeleteWaste());
	}

	@AgentKilled
	public void killed() {

	}

	public void dumpWaste(int quantity) {
		wasteDumped += quantity;
	}

	/**
	 * 
	 * @return a copy of the agent location
	 */
	public Location getLocation() {
		return position.clone();
	}

	/**
	 * @return the wasteDumped
	 */
	public int getWasteDumped() {
		return wasteDumped;
	}

	public void burnWaste() {
		if (wasteDumped >= 10)
			wasteDumped -= 10;
		else {
			wasteDumped = 0;
		}
	}

	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class DeleteWaste {

	}

}
