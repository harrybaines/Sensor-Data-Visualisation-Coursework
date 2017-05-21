import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * A class to model a simple bar graph component.
 * Multiple instances of this class will aid in populating an easy to use UI on the statistics panel of the application.
 * The graph displays all the errors found in the sensor data and outputs a simple bar graph.
 *
 * @author Harry Baines
 */
public class BarGraphComponent extends JPanel 
{
	// Instance variables - graph details variables
	private String title;
	private int[] values;
	private int barType;
	private final String[] errorXAxisNames;
	private final String[] sensorXAxisNames;
	private String[] namesUsing;

	// General graph variables
	private double maxValue;
	private double minValue;
	private Dimension dim;
	private int width;
	private int height;
	private int barWidth;
	private FontMetrics titleFontMetrics;
	private FontMetrics labelFontMetrics;
	private int titleWidth;
	private int x;
	private int y;
	private int top;
	private int bottom;
	private double scale;
	private int valueX;
	private int valueY;
	private int valueHeight;
	private int labelWidth;

	/**
	 * A simple constructor to initialise all the relevant graph variables such as the title details and the axis labels.
	 *
	 * @param values The integer array of values to use in the plot.
	 * @param title The title detailing what the bar graph displays.
	 * @param barType An integer value to determine which set of X axis labels to use.
	 */
	public BarGraphComponent(int[] values, String title, int barType) 
	{
		this.title = title;
		this.values = values;
		this.barType = barType;
		this.errorXAxisNames = new String[] {"Records Found", "Errors Found", "Unique Errors Found"};
		this.sensorXAxisNames = new String[10];

		for (int i = 1; i <= 10; i++)
			sensorXAxisNames[i-1] = "Sensor " + i;
	}

	/**
	 * Method to paint a bar graph component onto the UI.
	 * @param g The graphics component instance to paint onto the UI.
	 */
	protected void paintComponent(Graphics g) 
	{
		// Override method
		super.paintComponent(g);

		// Find max and min values
		maxValue = 0;
		minValue = 0;
		for (int i = 0; i < values.length; i++) {
			if (maxValue < values[i])
				maxValue = values[i];
			if (minValue > values[i])
				minValue = values[i];
		}

		// Dimensions and sizes
		dim = getSize();
		width = dim.width;
		height = dim.height;
		barWidth = width / values.length;

		// Title label details
		titleFontMetrics = g.getFontMetrics(new Font("SansSerif", Font.BOLD, 20));
		labelFontMetrics = g.getFontMetrics(new Font("SansSerif", Font.PLAIN, 15));
		titleWidth = titleFontMetrics.stringWidth(title);
		x = (width - titleWidth)/2;
		y = titleFontMetrics.getAscent();
		g.setFont(new Font("SansSerif", Font.BOLD, 20));
		g.drawString(title, x, y);

		// Scale and position variables
		top = titleFontMetrics.getHeight();
		bottom = labelFontMetrics.getHeight();

		scale = (height - top - bottom) / (maxValue - minValue);
		y = height - labelFontMetrics.getDescent();
		g.setFont(new Font("SansSerif", Font.PLAIN, 15));

		// Plot X axis names depending on bar type
		switch (barType) {
			case 1:
				namesUsing = errorXAxisNames;
				break;
			case 2:
				namesUsing = sensorXAxisNames;
				break;
		}

		// Draw bars for each X value
		for (int i = 0; i < values.length; i++) {
			// Calculate width/height variables for bars
			valueX = i * barWidth + 1;
			valueY = top;
			valueHeight = (int) (values[i] * scale);

			if (values[i] >= 0)
				valueY += (int) ((maxValue - values[i]) * scale);
			else {
				valueY += (int) (maxValue * scale);
				height = -height;
			}

			// Bar colors and label strings
			g.setColor(Color.red);
			g.fillRect(valueX, valueY, barWidth - 2, valueHeight);

			g.setColor(Color.black);
			g.drawRect(valueX, valueY, barWidth - 2, valueHeight);

			labelWidth = labelFontMetrics.stringWidth(namesUsing[i]);
			x = i * barWidth + (barWidth - labelWidth) / 2;
			g.drawString(namesUsing[i], x, y);
		}

		// Write error total inside bars
		for (int i = 0; i < namesUsing.length; i++) {
			labelWidth = labelFontMetrics.stringWidth(namesUsing[i]);
			x = i * barWidth + (barWidth - labelWidth) / 2;
			g.drawString(Integer.toString(values[i]), x, y - (top));
		}
	}
}