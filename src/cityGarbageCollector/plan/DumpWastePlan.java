package cityGarbageCollector.plan;

import cityGarbageCollector.GCollector;
import cityGarbageCollector.Location;
import cityGarbageCollector.agent.BurnerBDI;
import cityGarbageCollector.agent.CollectorBDI;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;

@Plan
public class DumpWastePlan {

	@PlanCapability
	private CollectorBDI collector;

	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public DumpWastePlan() {
		//System.out.println("created: " + this);
	}

	// -------- methods --------


	public void DumpWastetoBurner(BurnerBDI burner) {	
		int q = collector.getActualWasteQuantity();
		burner.dumpWaste(q);
		collector.dump();
		
		System.out.println("Dumped: "+ q);
	}

	/**
	 * The plan body.
	 * @throws InterruptedException 
	 */
	@PlanBody
	public void body() throws InterruptedException {
		//System.out.println("DumpWastePlan body!");
		
		Location[] burnerlocations = GCollector.getInstance().getBurnerlocations();
		Location loc = collector.getLocation();

		for(int i=0; i<burnerlocations.length;i++) {
			boolean found=false;
			if( (loc.x+1 == burnerlocations[i].x) && (loc.y == burnerlocations[i].y) )
				found=true;
			else if( (loc.x == burnerlocations[i].x) && (loc.y+1 == burnerlocations[i].y) )
				found=true;
			else if( (loc.x-1 == burnerlocations[i].x) && (loc.y == burnerlocations[i].y) )
				found=true;
			else if( (loc.x == burnerlocations[i].x) && (loc.y-1 == burnerlocations[i].y) )
				found=true;

			if(found==true) {
				System.out.println("------Burner Encontrado!");
				BurnerBDI b = GCollector.getInstance().getBurnerByLocation(burnerlocations[i]);
				DumpWastetoBurner(b);
				collector.aux=false;
			}
		}
		//Thread.sleep((long) (CollectorBDI.SLEEP_MILLIS / GCollector.getInstance().speed()));
	}
}