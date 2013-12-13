package cityGarbageCollector.agent;

import java.util.LinkedList;

import sun.awt.SunToolkit.InfiniteLoop;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Goal.ExcludeMode;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.rules.eca.annotations.Event;
import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import cityGarbageCollector.Vertex;
import cityGarbageCollector.plan.DumpWastePlan;
import cityGarbageCollector.plan.GoToBurnerPlan;
import cityGarbageCollector.plan.PickUpWastePlan;
import cityGarbageCollector.plan.Wander;

@Agent
@Plans({ @Plan(trigger = @Trigger(goals = CollectorBDI.PerformPatrol.class), body = @Body(Wander.class)),
	@Plan(trigger = @Trigger(goals = CollectorBDI.CheckContainer.class), body = @Body(PickUpWastePlan.class)),
	@Plan(trigger = @Trigger(goals = CollectorBDI.GoToBurnerGoal.class), body = @Body(GoToBurnerPlan.class)),
	@Plan(trigger = @Trigger(goals = CollectorBDI.DumpGoal.class), body = @Body(DumpWastePlan.class)) })
public class CollectorBDI {

	@Agent
	protected BDIAgent agent;

	private String name;
	
	public boolean aux = false;

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
		position = new Location(0, 0);
		steps = new LinkedList<>();
		capacity = 50;
		actualWasteQuantity = 0;
		GCollector.getInstance().addCollectorAgent(this);
	}



	@AgentBody
	public void body() {
		agent.dispatchTopLevelGoal(new CheckContainer());
		agent.dispatchTopLevelGoal(new PerformPatrol());
		agent.dispatchTopLevelGoal(new DumpGoal());
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

	public void pickWaste(int quantity) {
		if(quantity>0) {
			actualWasteQuantity+=quantity;
			if(actualWasteQuantity == capacity)
				full=true;
		}
	}

	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class CheckContainer {
		/**
		 * Suspend the goal when on pause.
		 */
		@GoalContextCondition(rawevents = "pause")
		public boolean checkContext(){
			return (!pause && !full);
		}
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


	/**
	 * Create a new goal whenever full belief is changed.
	 */
	// @Goal(deliberation=@Deliberation(inhibits={PerformPatrol.class, CheckContainer.class}))
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
			/*
			// se full define os steps para o burner +proximo
			if (full) { // SUFICIENTE??
				Location loc = getNearestBurner();
				goToLocation(loc);
			}
			 */
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

		/**
		 * Suspend the goal when on pause.
		 */
		@GoalContextCondition(rawevents = "pause")
		public boolean checkContext() {
			return (!pause && !full);
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
		int lessSteps = Integer.MAX_VALUE;
		Location nearestLoc = null;

		for (int i = 0; i < burnerlocations.length; i++) {
			Location[] locs = new Location[4];
			locs[0] = new Location(burnerlocations[i].x+1, burnerlocations[i].y);
			locs[1] = new Location(burnerlocations[i].x, burnerlocations[i].y+1);
			locs[2] = new Location(burnerlocations[i].x-1, burnerlocations[i].y);
			locs[3] = new Location(burnerlocations[i].x, burnerlocations[i].y-1);

			for(int j=0; j < 4; j++) {
				if(GCollector.getInstance().isRoadOnLocation(locs[j]))
					if ((GCollector.getInstance().getAgentTrip(position, locs[j])).size() < lessSteps)
						nearestLoc=locs[j];
			}
		}
		return nearestLoc;
	}

}
