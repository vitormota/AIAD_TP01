package garbagecollector;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.Map;

/**
 *  The go action for moving one field in one of four directions.
 */
public class GoAction extends SimplePropertyObject implements ISpaceAction
{
	//-------- constants --------

	/** The directions. */
	public static final String UP = "up";
	public static final String DOWN = "down";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";

	public static final String DIRECTION = "direction";
	
	
	//-------- methods --------
	
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		
		String dir = (String)parameters.get(DIRECTION);
		Object oid = parameters.get(ISpaceAction.OBJECT_ID);
		ISpaceObject obj = space.getSpaceObject(oid);
		IVector2 pos = (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		
		int px = pos.getXAsInteger();
		int py = pos.getYAsInteger();
		
		switch(dir){
		case UP:
			pos = new Vector2Int(px, py-1);
			break;
		case DOWN:
			pos = new Vector2Int(px, py+1);
			break;
		case LEFT:
			pos = new Vector2Int(px-1, py);
			break;
		case RIGHT:
			pos = new Vector2Int(px+1, py);
			break;
		}
	
		((Space2D)space).setPosition(oid, pos);
		obj.setProperty("lastmove", dir);
		
		return null;
	}

	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		return "go";
	}
}
