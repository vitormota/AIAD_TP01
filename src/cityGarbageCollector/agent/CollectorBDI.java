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
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.rules.eca.annotations.Event;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import cityGarbageCollector.ChatService;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.rules.eca.annotations.Event;

import java.util.LinkedList;

import com.sun.org.glassfish.gmbal.Description;

import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import cityGarbageCollector.Vertex;
import cityGarbageCollector.plan.DumpWastePlan;
import cityGarbageCollector.plan.GoToBurnerPlan;
import cityGarbageCollector.plan.PickUpWastePlan;
import cityGarbageCollector.plan.Wander;

@Agent
@Description("Trash collector agent")
@ProvidedServices(@ProvidedService(type=IChatService.class, implementation=@Implementation(ChatService.class)))
@RequiredServices({
@RequiredService(name="chatservices", type=IChatService.class, multiple=true,
	binding=@Binding(dynamic=true, scope=Binding.SCOPE_PLATFORM))
})
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
	
	public boolean aux = false;

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
	
	@Belief
	private static final int nrMsg=0;

	@AgentCreated
	public void init() {
		position = (Location) agent.getArgument("Location");
		if(position == null){
			position = new Location(0, 0);
		}
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
		agent.dispatchTopLevelGoal(new DumpGoal());
		// System.out.println("agentbody");
		//sendMessage("OKKKK");
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
	

	@Goal
	public class PickUpWaste {

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
	
	
	public String getLocalName() {
		return agent.getComponentIdentifier().getLocalName();
	}
	
	/** The underlying micro agent. */
	//@Agent
	//protected MicroAgent agent;
 
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	private void sendMessage(final String text, final boolean first) {
		IFuture<Collection<IChatService>>	chatservices	= agent.getServiceContainer().getRequiredServices("chatservices");
		chatservices.addResultListener(new DefaultResultListener<Collection<IChatService>>()
		{
			public void resultAvailable(Collection<IChatService> result)
			{
				for(Iterator<IChatService> it=result.iterator(); it.hasNext(); ) {
					IChatService cs = it.next();
					cs.message(agent.getComponentIdentifier().getLocalName(), text, first);
				}
			}
		});
	}

	public void receiveMessage(String nick, String text, boolean first) {
		if(nick != getLocalName())
		{
			String[] msg = text.split("-");
			if(first) {
				
			}
			else {
				
			}
			
			
		}
	}
}
