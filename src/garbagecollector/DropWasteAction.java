package garbagecollector;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.Map;

/**
 *  Action for dropping waste on the robots field.
 */
public class DropWasteAction extends SimplePropertyObject implements ISpaceAction
{
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		
		Grid2D grid = (Grid2D)space;
		
		IComponentDescription owner = (IComponentDescription)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject so = grid.getAvatar(owner);
		IVector2 pos = (IVector2)so.getProperty(Grid2D.PROPERTY_POSITION);
		
		//If there is no garbage raise exception (if that is the case this action shouldn't be called)
		assert so.getProperty("garbage") != null;

		ISpaceObject garb = (ISpaceObject)so.getProperty("garbage");
		grid.setPosition(garb.getId(), pos);
		so.setProperty("garbage", null);
		
		return null;
	}

	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		return "drop";
	}
}
