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
    // Linked list to store sensor data and iterator
    private LinkedList<Integer> sensorPoints = new LinkedList<Integer>();
    private ListIterator<Integer> listIt;

    // Graph variables
    private final int PAD = 40;
    private double xInc;
    private double scale;
    private int w;
    private int h;
    private double x;
    private double y;
    private int sensorPoint;
    private int i;

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
        w = getWidth();
        h = getHeight();

        // Space between each point
        xInc = (double) (w - 2*PAD) / (sensorPoints.size() - 1);

        // Scale: padding - maximum point value
        scale = (double) (h - 2*PAD) / 300;

        // Axis lines
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
        g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));
        g2.draw(new Line2D.Double(PAD, PAD, PAD-5, PAD+5));
        g2.draw(new Line2D.Double(PAD, PAD, PAD+5, PAD+5));
        g2.draw(new Line2D.Double(w-PAD, h-PAD, w-PAD-5, h-PAD-5));
        g2.draw(new Line2D.Double(w-PAD, h-PAD, w-PAD-5, h-PAD+5));

        // Y axis labels
        for (int i = 0; i < 7; i++)
            g2.drawString(Integer.toString(i*50), PAD/4, h - PAD - (int)(i*scale*50));

        // X axis labels
        for (int i = 0; i < 8; i++)
            g2.drawString(Integer.toString((i+1)*100), (i*100) + 80, getHeight() - 20);

        // Iterate over all data points and mark data points with ellipses
        i = 0;
        g2.setPaint(Color.red);
        listIt = sensorPoints.listIterator();
        while (listIt.hasNext())
        {
            sensorPoint = listIt.next();
            x = PAD + i*xInc;
            y = h - PAD - scale*sensorPoint;
            g2.fill(new Ellipse2D.Double(x-2,y-2,1.5,1.5));
            i++;
        }
    }

    /**
     * Constructor to initialise sensor data with relevant data for that particular sensor.
     */
    public LineGraphComponent(LinkedList<Integer> sensorPoints)
    {
        this.sensorPoints = sensorPoints;
    }
}