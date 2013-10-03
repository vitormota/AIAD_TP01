package garbagecollector;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;

import java.util.HashMap;
import java.util.Map;

/**
 *  Try to pickup some piece of garbage.
 */
public class PickUpPlanEnv extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{

		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("env").getFact();
		
		Map params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
		SyncResultListener srl	= new SyncResultListener();
		env.performSpaceAction("pickup", params, srl);
		if(!((Boolean)srl.waitForResult()).booleanValue()) 
			fail();
		
	}
}
