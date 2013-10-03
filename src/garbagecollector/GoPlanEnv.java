package garbagecollector;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;

/**
 *  Go to a specified position.
 */
public class GoPlanEnv extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Grid2D env = (Grid2D)getBeliefbase().getBelief("env").getFact();
		
		//Get the position, as specified in the XML
		IVector2 target = (IVector2)getParameter("pos").getValue();
		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		
		while(!target.equals(myself.getProperty(Space2D.PROPERTY_POSITION)))
		{
			//Calculate de the new direction
			String dir = null;
			IVector2 mypos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			
			IVector1 md = env.getShortestDirection(mypos.getX(), target.getX(), true);
			
			switch(md.getAsInteger()){
			case 1:
				dir = GoAction.RIGHT;
				break;
			case -1:
				dir = GoAction.LEFT;
				break;
			default:
				md = env.getShortestDirection(mypos.getY(), target.getY(), false);
				switch(md.getAsInteger()){
				case 1:
					dir = GoAction.DOWN;
					break;
				case -1:
					dir = GoAction.UP;
					break;
				}
			}
			
			//Inform what is the new direction and execute space action
			Map params = new HashMap();
			params.put(GoAction.DIRECTION, dir);
			params.put(ISpaceAction.OBJECT_ID, env.getAvatar(getComponentDescription()).getId());
			SyncResultListener srl	= new SyncResultListener();
			env.performSpaceAction("go", params, srl); 
			srl.waitForResult();
		}
	}
}

