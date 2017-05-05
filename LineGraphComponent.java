import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

/**
 * A class to model a simple line graph component.
 * The graph displays all provided sensor data points in a compact component and can be viewed inside a panel/frame.
 *
 * @author Harry Baines
 */
public class LineGraphComponent extends JPanel 
{
    // Linked list to store sensor data and iterator to iterate over the linked list
    private LinkedList<Integer> sensorPoints = new LinkedList<Integer>();
    private ListIterator<Integer> listIt;
    private LinkedList<String> datePoints = new LinkedList<String>();
    private ListIterator<String> listItDates;
    private String title_details;

    // Graph variables
    private final int pad = 40;
    private double xInc;
    private double scale;
    private int width;
    private int height;
    private double xPos;
    private double yPos;
    private int sensorPoint;
    private String datePoint;
    private int inc;
    private int i = 0;

    /**
     * Method to paint a scatter graph component on the UI.
     * @param g The graphics component instance to paint onto the UI.
     */
    protected void paintComponent(Graphics g) 
    {
        // Override method
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        // Width and height of component
        width = getWidth();
        height = getHeight();

        // Space between each point
        xInc = (double) (width - 2*pad) / (sensorPoints.size() - 1);

        // Scale: padding - maximum point value
        scale = (double) (height - 2*pad) / 300;

        // Axis lines
        g2.draw(new Line2D.Double(pad*2, pad, pad*2, height-pad));
        g2.draw(new Line2D.Double(pad*2, height-pad, width-pad, height-pad));
        g2.draw(new Line2D.Double(pad*2, pad, pad*2-5, pad+5));
        g2.draw(new Line2D.Double(pad*2, pad, pad*2+5, pad+5));
        g2.draw(new Line2D.Double(width-pad, height-pad, width-pad-5, height-pad-5));
        g2.draw(new Line2D.Double(width-pad, height-pad, width-pad-5, height-pad+5));

        // Title label
		g2.setFont(new Font("Verdana", Font.PLAIN, 22)); 
        g2.drawString(title_details, (width/2) - 200, 30);

        // Y axis labels
        g2.setFont(new Font("Verdana", Font.PLAIN, 18)); 
        for (int i = 0; i < 7; i++)
            g2.drawString(Integer.toString(i*50), pad, height - pad - (int)(i*scale*50));

		// Y axis value label
		g2.setFont(new Font("Verdana", Font.PLAIN, 16)); 
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(-90), 0, 0);
		g2.setFont(g2.getFont().deriveFont(affineTransform));
		g2.drawString("Value", pad/2 + 5, height/2 + 20);

		// Data points - iterate over all data points and mark data points with red ellipses
        inc = 0;
        g2.setPaint(Color.red);
        listIt = sensorPoints.listIterator();
        while (listIt.hasNext())
        {
            sensorPoint = listIt.next();
            xPos = pad*2 + inc*xInc;
            yPos = height - pad - scale*sensorPoint;
            g2.fill(new Ellipse2D.Double(xPos-2,yPos-2,1.5,1.5));
            inc++;
        }

        // X axis labels - iterate over all date data points and mark on X axis
        i = 0;
        g2.setPaint(Color.BLACK);
        g2.setFont(new Font("default", Font.BOLD, 11));
        listItDates = datePoints.listIterator();
        while (listItDates.hasNext())
        {
            datePoint = listItDates.next();
            g2.drawString(datePoint, pad*2 + (i*170), getHeight() - 20);
            i++;
        }
    }

    /**
     * Constructor to initialise sensor data with relevant data for that particular sensor.
     *
     * @param sensorPoints The linked list of sensor data points to plot over time. 
     * @param datePoints The linked list of date strings to plot on the X axis.
     * @param device_address The string address of the device for which data is being plotted.
     */
    public LineGraphComponent(LinkedList<Integer> sensorPoints, LinkedList<String> datePoints, String title_details)
    {
        this.sensorPoints = sensorPoints;
        this.datePoints = datePoints;
        this.title_details = title_details;
    }
}