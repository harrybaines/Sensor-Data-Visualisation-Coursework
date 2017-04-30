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
        g2.draw(new Line2D.Double(pad, pad, pad, height-pad));
        g2.draw(new Line2D.Double(pad, height-pad, width-pad, height-pad));
        g2.draw(new Line2D.Double(pad, pad, pad-5, pad+5));
        g2.draw(new Line2D.Double(pad, pad, pad+5, pad+5));
        g2.draw(new Line2D.Double(width-pad, height-pad, width-pad-5, height-pad-5));
        g2.draw(new Line2D.Double(width-pad, height-pad, width-pad-5, height-pad+5));

        // Y axis labels
        for (int i = 0; i < 7; i++)
            g2.drawString(Integer.toString(i*50), pad/4, height - pad - (int)(i*scale*50));

        // X axis labels
        for (int i = 0; i < 8; i++)
            g2.drawString(Integer.toString((i+1)*100), (i*100) + 80, height - 20);

        // Iterate over all data points and mark data points with red ellipses
        inc = 0;
        g2.setPaint(Color.red);
        listIt = sensorPoints.listIterator();
        while (listIt.hasNext())
        {
            sensorPoint = listIt.next();
            xPos = pad + inc*xInc;
            yPos = height - pad - scale*sensorPoint;
            g2.fill(new Ellipse2D.Double(xPos-2,yPos-2,1.5,1.5));
            inc++;
        }

        // Iterate over all date data points and mark on X axis
        i = 0;
        g2.setPaint(Color.BLACK);
        listItDates = datePoints.listIterator();
        while (listItDates.hasNext())
        {
            datePoint = listItDates.next();
            g2.drawString(datePoint, (i*100) + 80, getHeight() - 20);
            i++;
        }
    }

    /**
     * Constructor to initialise sensor data with relevant data for that particular sensor.
     */
    public LineGraphComponent(LinkedList<Integer> sensorPoints, LinkedList<String> datePoints)
    {
        this.sensorPoints = sensorPoints;
        this.datePoints = datePoints;
    }
}