import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

public class ScatterGraphComponent extends JPanel 
{
    private LinkedList<Integer> sensorPoints = new LinkedList<Integer>();
    private ListIterator<Integer> listIt;
    private final int PAD = 40;

 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // Width and height of component
        int w = getWidth();
        int h = getHeight();

        // Space between each point
        double xInc = (double) (w - 2*PAD) / (sensorPoints.size()-1);

        // Scale: padding - maximum point value
        double scale = (double)(h - 2*PAD)/300;

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

        


        // Mark data points.
        g2.setPaint(Color.red);

        int i = 0;

        listIt = sensorPoints.listIterator();

        while (listIt.hasNext())
        {
            int sensorPoint = listIt.next();
            double x = PAD + i*xInc;
            double y = h - PAD - scale*sensorPoint;
            g2.fill(new Ellipse2D.Double(x-2,y-2,1.5,1.5));
            i++;
        }

    }

    /**
     * Constructor to initialise sensor data with relevant data for that particular sensor.
     */
    public ScatterGraphComponent(LinkedList<Integer> sensorPoints)
    {
        this.sensorPoints = sensorPoints;
        listIt = sensorPoints.listIterator();

        System.out.println("NEW SENSOR POINTS!!!\n");

        while (listIt.hasNext())
        {
            int sensorPoint = listIt.next();
            System.out.println("Sensor Point: " + sensorPoint);
        }
    }



}