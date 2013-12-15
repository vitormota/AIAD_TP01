package cityGarbageCollector.agent;

import cityGarbageCollector.GCollector;
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
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;


@Agent
@Plans({ 
	@Plan(trigger = @Trigger(goals = ContainerBDI.PerformCreateWaste.class), body = @Body(CreateWaste.class)) })
@Arguments({
	@Argument(name="Location", clazz=Location.class),
	@Argument(name="Type", clazz=CollectorBDI.Trash_Type.class, defaultvalue="Common")
})
public class ContainerBDI {

	public static final String CLASS_PATH = "cityGarbageCollector/agent/ContainerBDI.class";
	
	@Agent
	protected BDIAgent agent;

	private int cont = 0;
	
	@Belief
	private Location position;
	@Belief
	private boolean pause = false;
	@Belief
	private int wasteQuantity;
	
	@Belief
	public static final long SLEEP_MILLIS = 500;

	public int getWasteQuantity() {
		return wasteQuantity;
	}

	@AgentCreated
	public void init() {
		position = (Location) agent.getArgument("Location");
		if(position == null){
			position = new Location(1, 0);
		}
		wasteQuantity=60;
		GCollector.getInstance().addContainerAgent(this);
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
