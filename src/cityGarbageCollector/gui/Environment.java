package cityGarbageCollector.gui;

import jadex.bridge.IExternalAccess;
import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.util.LinkedList;

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
	private AgentOptions southPanel;
	private GlobalOptions eastPanel;
	
	/**
	 *  ONLY FOR TESTING
	 * @throws FileNotFoundException 
	 */
	public Environment(final IExternalAccess agent) throws FileNotFoundException {
		// TODO Auto-generated constructor stub
		city = new City("maps/roadmap_1.map");
		city.drawGrid();
		city.drawMap();
		southPanel = new AgentOptions();
		eastPanel = new GlobalOptions();
		getContentPane().add(city,BorderLayout.CENTER);
		getContentPane().add(southPanel,BorderLayout.SOUTH);
		getContentPane().add(eastPanel,BorderLayout.EAST);
		setSize(600, 600);
		setLocation(SGUI.calculateMiddlePosition(Environment.this));
		setVisible(true);
		setTitle("FEUP - 2013/2014 - AIAD: City Garbage Collector");
		
		pack();
	}
	
	public int getCityWidth(){
		return city.getSize_w();
	}
	
	public void setAgentPosTextFields(int x, int y){
		southPanel.setAgentLocation(x, y);
	}
	
	public void setModifyPosTextFields(int x,int y){
		eastPanel.setModifyLocation(x,y);
	}
	
	public int getCityHeight(){
		return city.getSize_h();
	}
	
	public Gridpanel getCitySpacebyLocation(Location l){
		return city.getSpaceByLocation(l);
	}
	
	public LinkedList<Location> getAgentTrip(Location loc){
		return city.getAgentTrip(loc);
	}

	public LinkedList<Location> getAgentTrip(Location pos, Location dest) {
		return city.getAgentTrip(pos,dest);
	}

}
