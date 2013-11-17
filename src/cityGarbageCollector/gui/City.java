package cityGarbageCollector.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import cityGarbageCollector.Location;

public class City extends JPanel {
	/**
	 * City size in blocks width
	 */
	private int size_w;
	/**
	 * City size in blocks height
	 */
	private int size_h;

	/**
	 * the city grid
	 */
	private Gridpanel[][] city_squares;

	/**
	 * Start a new instance of the city road map, witch is a grid
	 * 
	 * @param width
	 *            in blocks
	 * @param height
	 *            in blocks
	 */
	public City(int width, int height) {
		this.size_w = width;
		this.size_h = height;
		city_squares = new Gridpanel[getSize_w()][getSize_w()];
	}

	/**
	 * version 0.1 draw a simple grid
	 */
	public void drawGrid() {
		setLayout(new GridLayout(getSize_h(), getSize_w()));
		for (int i = 0; i < getSize_h(); i++) {
			for (int j = 0; j < getSize_w(); j++) {
				city_squares[i][j] = new Gridpanel(i, j);
				add(city_squares[i][j]);
			}
		}
	}
	
	public void unpaintAgent(Location loc){
		city_squares[loc.y][loc.x].img = null;
		city_squares[loc.y][loc.x].repaint();
	}
	
	public void paintAgent(Location loc) {
		// TODO Auto-generated method stub
		city_squares[loc.y][loc.x].setImage("images/collector.png");
		city_squares[loc.y][loc.x].repaint();
	}

	/**
	 * @return the size_h
	 */
	public int getSize_h() {
		return size_h;
	}

	/**
	 * @return the size_w
	 */
	public int getSize_w() {
		return size_w;
	}

	private class Gridpanel extends JPanel implements MouseListener {
		public int x, y;
		public Image img;

		public Gridpanel(int x, int y) {
			// TODO Auto-generated constructor stub
			this.x = x;
			this.y = y;
			this.setBorder(BorderFactory.createLineBorder(Color.RED));
			addMouseListener(this);
		}

		public void setImage(String path) {
			this.img = new ImageIcon(path).getImage();
		}

		public void paintComponent(Graphics g) {
			g.drawImage(img, 0, 0, null);
			System.out.println("paint component");
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Clicked on: " + x + "," + y);
		}
	}

	

}
