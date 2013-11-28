package cityGarbageCollector.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

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

	public Gridpanel getSpaceByLocation(Location l) {
		return city_squares[l.y][l.x];
	}

	public class Gridpanel extends JLabel implements MouseListener {
		public int x, y;
		public Image img;
		/**
		 * Flags for objects on this space
		 */
		public boolean collector = false, burner = false, container = false;

		public Gridpanel(int x, int y) {
			// TODO Auto-generated constructor stub
			this.x = x;
			this.y = y;
			this.setBorder(BorderFactory.createLineBorder(Color.RED));
			addMouseListener(this);
		}

		public void updateImage() throws IOException {
			if (collector) {
				setIcon(new ImageIcon(ImageIO.read(new File("images/collector.png"))));
				return;
			}
			if (burner) {
				return;
			}
			if (container) {
				setIcon(new ImageIcon(ImageIO.read(new File("images/garbage.png"))));
				return;
			}
			// if none, then put background back
			setIcon(null);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Clicked on: " + x + "," + y);
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

		}
	}

}
