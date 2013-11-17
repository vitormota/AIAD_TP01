package cityGarbageCollector.agent;

import javax.swing.JOptionPane;

import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import cityGarbageCollector.plan.Wander;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Goal.ExcludeMode;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;

@Agent
@Plans({
	@Plan(trigger=@Trigger(goals=CollectorBDI.PerformPatrol.class),body=@Body(Wander.class))
})
public class CollectorBDI {

	@Agent
	protected BDIAgent agent;
	
	@Belief
	private Location position;
	@Belief
	private boolean pause=false;
	
	@AgentCreated
	public void init(){
		position = new Location(0, 0);
		GCollector.getInstance().addAgent(this);
	}
	
	@AgentBody
	public void body(){
		agent.dispatchTopLevelGoal(new PerformPatrol()).get();
	}
	
	@AgentKilled
	public void	killed()
	{
		
	}
	
	public void updatePosition() throws InterruptedException{
		Thread.sleep(1000);
		GCollector.getInstance().env.unpaintAgent(position);
		this.position.autoMove();
		GCollector.getInstance().env.paintAgent(position);
		
		//DEBUG
		System.out.println(position);
	}
	
	@Goal(excludemode=ExcludeMode.Never,retry=true, succeedonpassed=false)
	public class PerformPatrol {
		
		/**
		 *  Suspend the goal when on pause.
		 */
		@GoalContextCondition(events="pause")
		public boolean checkContext()
		{
			return !pause;
		}

	}
}
