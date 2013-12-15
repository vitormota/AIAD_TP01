package cityGarbageCollector.agent;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Goal.ExcludeMode;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

import tutorial.LineChartDemo1;
import cityGarbageCollector.GCollector;
import cityGarbageCollector.plan.ChartUpdate;

import com.sun.org.glassfish.gmbal.Description;

@Agent
@Description("Agent that collects information about other agents creating and updating charts")
@Plans({ @Plan(trigger = @Trigger(goals = ChartsBDI.ChartUpdateGoal.class), body = @Body(ChartUpdate.class)) })
public class ChartsBDI {

	@Agent
	protected BDIAgent agent;

	// ================================================================================
	// Beliefs
	// ================================================================================

	/**
	 * The update chart rate
	 */
	@Belief
	public static final long SLEEP_MILLIS = 5000;

	@Belief
	private int update_count = 0;

	@Belief
	private DefaultCategoryDataset dataset;

	// ================================================================================
	// Operations
	// ================================================================================

	@AgentCreated
	public void init() {
		dataset = new DefaultCategoryDataset();
		ApplicationFrame af = new ApplicationFrame("AIAD - 2013/2014");
		JPanel chartPanel = createChartPanel();
		chartPanel.setPreferredSize(new Dimension(500, 400));
//		af.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		af.setContentPane(chartPanel);
		af.pack();
		af.setVisible(true);
	}

	@AgentBody
	public void body() {
		agent.dispatchTopLevelGoal(new ChartUpdateGoal()).get();
	}

	/**
	 * Creates a panel for the chart.
	 * 
	 * @return A panel.
	 */
	public JPanel createChartPanel() {
		JFreeChart chart = createChart(dataset);
		ChartPanel panel = new ChartPanel(chart);
		// panel.setMouseWheelEnabled(true);
		return panel;
	}

	/**
	 * Creates chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * 
	 * @return The chart.
	 */
	private JFreeChart createChart(CategoryDataset dataset) {

		// create the chart...
		JFreeChart chart = ChartFactory.createLineChart("Trash chart", // chart
																		// title
				"Time elapsed (5 second step)", // domain axis label
				"Trash Count", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
				);

		chart.addSubtitle(new TextTitle("Amount of trash at a time"));
		TextTitle source = new TextTitle("AIAD: City Garbage Collector " + "by Vitor Mota & Jorge Reis");
		source.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 8));
		source.setPosition(RectangleEdge.BOTTOM);
		source.setHorizontalAlignment(HorizontalAlignment.CENTER);
		chart.addSubtitle(source);
		
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		// plot.setRangePannable(true);
		plot.setRangeGridlinesVisible(false);
		URL imageURL = LineChartDemo1.class.getClassLoader().getResource("OnBridge11small.png");
		if (imageURL != null) {
			ImageIcon temp = new ImageIcon(imageURL);
			// use ImageIcon because it waits for the image to load...
			chart.setBackgroundImage(temp.getImage());
			plot.setBackgroundPaint(null);
		}
		// customise the range axis...
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		ChartUtilities.applyCurrentTheme(chart);

		// customise the renderer...
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseShapesVisible(true);
		renderer.setDrawOutlines(true);
		renderer.setUseFillPaint(true);
		renderer.setBaseFillPaint(Color.white);
		renderer.setSeriesStroke(0, new BasicStroke(3.0f));
		renderer.setSeriesOutlineStroke(0, new BasicStroke(2.0f));
		renderer.setSeriesShape(0, new Ellipse2D.Double(-5.0, -5.0, 10.0, 10.0));
		return chart;
	}

	public void updateChart() {
		int trash[] = GCollector.getInstance().getTrashCount();
		int elapsed_time = (update_count++) * 5;
		dataset.addValue(trash[0], "Containers", "" + elapsed_time);
		dataset.addValue(trash[1], "Collectors", "" + elapsed_time);
		dataset.addValue(trash[2], "Burners", "" + elapsed_time);
		dataset.addValue(trash[3], "All", "" + elapsed_time);
	}

	// ================================================================================
	// Internal Goal class
	// ================================================================================

	@Goal(excludemode = ExcludeMode.Never, retry = true, succeedonpassed = false)
	public class ChartUpdateGoal {
		// Empty class, needed to tell the agent to pursue the goal and plan
		// forever
	}

}
