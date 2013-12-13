package cityGarbageCollector.agent;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Goal.ExcludeMode;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.rules.eca.annotations.Event;

import java.util.LinkedList;

import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import cityGarbageCollector.plan.DumpWastePlan;
import cityGarbageCollector.plan.GoToBurnerPlan;
import cityGarbageCollector.plan.PickUpWastePlan;
import cityGarbageCollector.plan.Wander;

@Agent
@Plans({ @Plan(trigger = @Trigger(goals = CollectorBDI.PerformPatrol.class), body = @Body(Wander.class)),
		@Plan(trigger = @Trigger(goals = CollectorBDI.CheckContainer.class), body = @Body(PickUpWastePlan.class)),
		@Plan(trigger = @Trigger(goals = CollectorBDI.GoToBurnerGoal.class), body = @Body(GoToBurnerPlan.class)),
		@Plan(trigger = @Trigger(goals = CollectorBDI.DumpGoal.class), body = @Body(DumpWastePlan.class)) })
@Arguments({
	@Argument(name="Location", clazz=Location.class),
	@Argument(name="Type", clazz=CollectorBDI.Trash_Type.class, defaultvalue="Common"),
	@Argument(name="Name", clazz=String.class, defaultvalue="Nameless"),
	@Argument(name="Capacity", clazz=Integer.class, defaultvalue="10")
})
public class CollectorBDI {

	public static final String CLASS_PATH = "cityGarbageCollector/agent/CollectorBDI.class";
	
	@Agent
	protected BDIAgent agent;

	@Belief
	public String name;
	
	public static enum Trash_Type{
		Common, Metal, Plastic, Paper
	}
	
	@Belief
	public Trash_Type type = Trash_Type.Common;

	@Belief
	private int capacity;
	@Belief
	public boolean full = false;
	@Belief
	private int actualWasteQuantity;
	@Belief
	private Location position;
	@Belief
	public boolean pause = false;
	private LinkedList<Location> steps;

	@Belief
	public static final long SLEEP_MILLIS = 500;

	@AgentCreated
	public void init() {
		position = (Location) agent.getArgument("Location");
		steps = new LinkedList<>();
		type = (Trash_Type) agent.getArgument("Type");
		capacity = (Integer) agent.getArgument("Capacity");
		name = (String) agent.getArgument("Name");
		actualWasteQuantity = 0;
		this.pause = GCollector.getInstance().getPauseState();
		GCollector.getInstance().addCollectorAgent(this);
	}
	
	@AgentBody
	public void body() {
		agent.dispatchTopLevelGoal(new CheckContainer());
		agent.dispatchTopLevelGoal(new PerformPatrol());
		// agent.dispatchTopLevelGoal(new DumpGoal());
		// System.out.println("agentbody");
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
		if (steps != null) {
			this.position = steps.removeFirst();
		} else {
			this.position.autoMove();
		}
		// DEBUG
		// System.out.println(position);
	}

	public void goToLocation(Location dest) {
		// ask for route
		steps = GCollector.getInstance().getAgentTrip(position, dest);
	}

	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class DumpGoal {
		/**
		 * Suspend the goal when on pause.
		 */
		@GoalContextCondition(rawevents = "pause")
		public boolean checkContext() {
			return !pause;
		}
	}

	public void pickWaste(int quantity) {
		actualWasteQuantity += quantity;
	}
	
	
	/**
	 * Create a new goal whenever full belief is changed.
	 */
	// @Goal(deliberation=@Deliberation(inhibits={PerformPatrol.class,
	// CheckContainer.class}))
	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class CheckContainer {

	}

	@Goal
	public class PickUpWaste {

	}

	/**
	 * Create a new goal whenever full belief is changed.
	 */
	// @Goal(deliberation=@Deliberation(inhibits={PerformPatrol.class,
	// CheckContainer.class}))
	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class GoToBurnerGoal {

		// /**
		// * When the chargestate is below 0.2
		// * the cleaner will activate this goal.
		// *
		// */
		// @GoalMaintainCondition(rawevents="actualWasteQuantity")
		// public boolean checkMaintain()
		// {
		// return (actualWasteQuantity < 50);
		// }
		//
		// /**
		// * The target condition determines when
		// * the goal goes back to idle.
		// */
		// @GoalTargetCondition(rawevents="actualWasteQuantity")
		// public boolean checkTarget()
		// {
		// return (actualWasteQuantity == 0);
		// }*/

		protected boolean fullHere;

		@GoalCreationCondition(rawevents = "full")
		public GoToBurnerGoal(@Event("full") boolean fullH) {
			fullHere = fullH;
			System.out.println("AQUII " + fullHere);

			// se full define os steps para o burner +proximo
			if (full) { // SUFICIENTE??
				Location loc = getNearestBurner();
				goToLocation(loc);
			}
		}

		/**
		 * The goal is achieved when the position
		 * of the cleaner is near to the target position.
		 * 
		 * @GoalContextCondition(rawevents="full")
		 *                                         public boolean checkTarget()
		 *                                         {
		 *                                         System.out.println(
		 *                                         "checktarget");
		 *                                         return !full;
		 *                                         }
		 */

		/**
		 * Drop the goal when collector is not full
		 */
		@GoalDropCondition(rawevents = "full")
		public boolean checkDrop() {
			System.out.println("goaldrop " + fullHere);
			return !full;
		}

	}

	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class PerformPatrol {

//		/**
//		 * Suspend the goal when on pause.
//		 */
//		@GoalContextCondition(rawevents = "pause")
//		public boolean checkContext() {
//			return (!pause && !full);
//		}

	}

	/**
	 * 
	 * @return a copy of the agent location
	 */
	public Location getLocation() {
		// TODO Auto-generated method stub
		return position.clone();
	}

	public int getRemainingCapacity() {
		return capacity - actualWasteQuantity;
	}
	
	public void togglePause() {
		pause = !pause;
	}

	public int getActualWasteQuantity() {
		return actualWasteQuantity;
	}

	public void dump() {
		actualWasteQuantity = 0;
		full = false;
	}

	public Location getNearestBurner() {
		Location[] burnerlocations = GCollector.getInstance().getBurnerlocations();
		int x = 0;
		LinkedList<Location> tempSteps = GCollector.getInstance().getAgentTrip(position, burnerlocations[0]);

		for (int i = 0; i < burnerlocations.length; i++) {
			if (GCollector.getInstance().getAgentTrip(position, burnerlocations[i]).size() < tempSteps.size()) {
				tempSteps = GCollector.getInstance().getAgentTrip(position, burnerlocations[i]);
				x = i;
			}
		}

		return burnerlocations[x];
	}

}
