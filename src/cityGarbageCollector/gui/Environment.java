package cityGarbageCollector.gui;

import jadex.bridge.IExternalAccess;
import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import cityGarbageCollector.Location;
import cityGarbageCollector.gui.City.Gridpanel;

/**
 * User interface, will contains every component for user interaction
 * and agent visualization
 * 
 * @author vitor_000
 *
 */
public class Environment extends JFrame{
	
	/**
	 * City panel 
	 */
	private City city;
	
	/**
	 *  ONLY FOR TESTING
	 */
	public Environment(final IExternalAccess agent) {
		// TODO Auto-generated constructor stub
		city = new City(10, 10);
		city.drawGrid();
		getContentPane().add(city,BorderLayout.CENTER);
		setSize(600, 600);
		setLocation(SGUI.calculateMiddlePosition(Environment.this));
		setVisible(true);
	}
	
	public int getCityWidth(){
		return city.getSize_w();
	}
	
	public int getCityHeight(){
		return city.getSize_h();
	}
	
	public Gridpanel getCitySpacebyLocation(Location l){
		return city.getSpaceByLocation(l);
	}

}
