package garbagecollector;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.Collection;
import java.util.Map;

/**
 *  Action for picking up waste.
 */
public class PickupWasteAction extends SimplePropertyObject implements ISpaceAction
{
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{	
		boolean ret = false;
				
		Grid2D grid = (Grid2D)space;
		
		IComponentDescription owner = (IComponentDescription)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject so = grid.getAvatar(owner);

		assert so.getProperty("garbage") == null: so;
		
		Collection wastes = grid.getSpaceObjectsByGridPosition((IVector2)so.getProperty(Grid2D.PROPERTY_POSITION), "garbage");
		ISpaceObject waste = (ISpaceObject)(wastes!=null? wastes.iterator().next(): null);

		if(wastes!=null)
		{

			wastes.remove(waste);
			so.setProperty("garbage", waste);
			
			grid.setPosition(waste.getId(), null);
			ret = true;


		}



		return ret? Boolean.TRUE: Boolean.FALSE;
	}
}
