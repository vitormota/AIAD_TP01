package cityGarbageCollector;

import java.util.ArrayList;

import cityGarbageCollector.agent.CollectorBDI;
import cityGarbageCollector.gui.Environment;

/**
 * Singleton superinformed class
 * 
 * @author vitor_000
 *
 */
public class GCollector {
	
	/**
	 *  singleton instance
	 */
	private static GCollector instance = null;
	
	public Environment env;
	
	private ArrayList<CollectorBDI> agents;
	
	protected GCollector(){
		agents= new ArrayList<>();
	}
	
	public static GCollector getInstance(){
		if(instance == null) instance = new GCollector();
		return instance;
	}

	public void addAgent(CollectorBDI agent){
		agents.add(agent);
	}

}
