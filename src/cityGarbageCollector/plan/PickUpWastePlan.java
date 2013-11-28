package cityGarbageCollector.plan;

import cityGarbageCollector.GContainer;
import cityGarbageCollector.Location;
import cityGarbageCollector.agent.CollectorBDI;
import cityGarbageCollector.agent.ContainerBDI;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;

@Plan
public class PickUpWastePlan {

	@PlanCapability
	private CollectorBDI collector;
	
	Location loc;

	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public PickUpWastePlan() {
		//System.out.println("created: " + this);
		loc = collector.getLocation();
	}

	// -------- methods --------


	public void getWaste(ContainerBDI container) {
		if(container.getWasteQuantity() <= collector.getRemainingCapacity()) {
			container.decrementWaste(container.getWasteQuantity());
			collector.pickWaste(container.getWasteQuantity());
		}
		else
		{
			
		}
	}
	/**
	 * The plan body.
	 * @throws InterruptedException 
	 */
	@PlanBody
	public void body() throws InterruptedException {
		System.out.println("at CheckContainer planbody");
		Location[] clocations = GContainer.getInstance().getAgentlocations();

		for(int i=0; i<clocations.length;i++) {
			if( (loc.x+1 == clocations[i].x) && (loc.y == clocations[i].y) )
				System.out.println("Container Encontrado!");
			else if( (loc.x == clocations[i].x) && (loc.y+1 == clocations[i].y) )
				System.out.println("Container Encontrado!");
			else if( (loc.x-1 == clocations[i].x) && (loc.y == clocations[i].y) )
				System.out.println("Container Encontrado!");
			else if( (loc.x == clocations[i].x) && (loc.y-1 == clocations[i].y) )
				System.out.println("Container Encontrado!");
		}
		
	}

}
