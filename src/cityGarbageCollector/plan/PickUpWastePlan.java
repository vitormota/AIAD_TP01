package cityGarbageCollector.plan;

import cityGarbageCollector.GCollector;
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

	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public PickUpWastePlan() {
		//System.out.println("created: " + this);
	}

	// -------- methods --------


	public void getWaste(ContainerBDI container) {
		System.out.println("at getWaste");
		int quantity=0;
		System.out.println(collector.getRemainingCapacity());
		System.out.println(container.getWasteQuantity());

		if(container.getWasteQuantity() < collector.getRemainingCapacity())
			quantity=container.getWasteQuantity();
		else 
			quantity=collector.getRemainingCapacity();

		System.out.println("quantity:"+quantity);
		container.decrementWaste(quantity);
		collector.pickWaste(quantity);
		System.out.println("Picked up: "+ quantity);
	}

	/**
	 * The plan body.
	 * @throws InterruptedException 
	 */
	@PlanBody
	public void body() throws InterruptedException {
		//System.out.println("at pickup planbody");
		Location[] clocations = GCollector.getInstance().getContainerlocations();
		Location loc = collector.getLocation();

		for(int i=0; i<clocations.length;i++) {

			if( (loc.x+1 == clocations[i].x) && (loc.y == clocations[i].y) ) {
				System.out.println("Container Encontrado!");
				ContainerBDI c = GCollector.getInstance().getContainerByLocation(clocations[i]);
				if(c.type==collector.type && !collector.full) {
					getWaste(c);
				}
				else {
					//envia msg para agentes do tipo do container
					CollectorBDI.nrMsg++;
					String tipoLixo = "";
					String text=(Integer.toString(CollectorBDI.nrMsg))+"-"+tipoLixo+"-"+Integer.toString((collector.getLocation()).x)+"-"+Integer.toString((collector.getLocation()).y);
					collector.sendMessage(text, true);
				}
			}
			else if( (loc.x == clocations[i].x) && (loc.y+1 == clocations[i].y) ) {
				System.out.println("Container Encontrado!");
				ContainerBDI c = GCollector.getInstance().getContainerByLocation(clocations[i]);
				getWaste(c);
			}
			else if( (loc.x-1 == clocations[i].x) && (loc.y == clocations[i].y) ) {
				System.out.println("Container Encontrado!");
				ContainerBDI c = GCollector.getInstance().getContainerByLocation(clocations[i]);
				getWaste(c);
			}
			else if( (loc.x == clocations[i].x) && (loc.y-1 == clocations[i].y) ) {
				System.out.println("Container Encontrado!");
				ContainerBDI c = GCollector.getInstance().getContainerByLocation(clocations[i]);
				getWaste(c);
			}
		}
	}

}