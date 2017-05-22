import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

/**
 * A class to model a simple graph component.
 * Depending on the user selection in the application, a line graph, scatter graph and bar graph can be drawn onto a separate pop up panel.
 * Dashed guidelines can be used to help with determining precise sensor values.
 * The graph displays all provided sensor data points in a compact component and can be viewed inside a panel/frame.
 *
 * @author Harry Baines
 */
public class GraphComponent extends JPanel
{
    // Linked lists to store sensor data, flagged data points and date points, with iterators to iterate over the linked lists
    private LinkedList<Integer> sensorPoints;
    private LinkedList<Integer> flaggedDataPoints;
    private LinkedList<String> datePoints;
    private ListIterator<Integer> listIt;
    private ListIterator<Integer> listItFlagged;
    private ListIterator<String> listItDates;

    // Graph variables
    private Graphics2D g2;
    private Graphics2D g2d;
    private Stroke dashed;
    private final int pad;
    private final int[] barWidths;
    private String title_details;
    private String graphDetails;
    private AffineTransform affineTransform;
    private double xInc;
    private double scale;
    private int barWidth;
    private int width;
    private int height;
    private double xPos;
    private double yPos;
    private int sensorPoint;
    private int flaggedPoint;
    private String datePoint;
    private int inc;
    private double initXPos;
    private double initYPos;
    private double dateInc;
    private boolean dashedBool;
    private boolean scatterBool;
    private boolean barBool;
    private int distToXAxis;

    /**
     * Method to paint the users chosen graph type selection onto the UI.
     * @param g The graphics component instance to paint onto the UI.
     */
    protected void paintComponent(Graphics g) 
    {
        // Override method
        super.paintComponent(g);
        g2 = (Graphics2D)g;

        // Width and height of component
        width = getWidth();
        height = getHeight();

        // Space between each point
        xInc = (double) (width - 4*pad) / (sensorPoints.size() - 1);

        // Space between each date
        dateInc = 140;

        // Scale: padding - maximum point value
        scale = (double) (height - 2*pad) / 300;

        // Bar width size - low/mid/high frequency plots
        if (sensorPoints.size() == 51)
        	barWidth = barWidths[0];
        else if (sensorPoints.size() == 101)
        	barWidth = barWidths[1];
        else if (sensorPoints.size() == 251)
        	barWidth = barWidths[2];

        // Axis lines
        g2.draw(new Line2D.Double(pad*2, pad, pad*2, height-pad));
        g2.draw(new Line2D.Double(pad*2, height-pad, width-pad*2, height-pad));
        g2.draw(new Line2D.Double(pad*2, pad, (pad*2)-5, pad+5));
        g2.draw(new Line2D.Double(pad*2, pad, (pad*2)+5, pad+5));
        g2.draw(new Line2D.Double(width-(pad*2), height-pad, width-((pad*2)+5), height-pad-5));
        g2.draw(new Line2D.Double(width-(pad*2), height-pad, width-((pad*2)+5), height-pad+5));

        // Title label details
		g2.setFont(new Font("Verdana", Font.PLAIN, 22)); 
        g2.drawString(title_details, (width/2) - 200, 30);
        g2.setFont(new Font("Verdana", Font.ITALIC, 13));
        g2.drawString("Blue = SUCCESS, Red = ERRORS FOUND", (width/2) - 135, 60);
        g2.setFont(new Font("Verdana", Font.BOLD, 16));
        g2.drawString(graphDetails, (width/2) + 400, 30);

        // Y axis labels and dashed lines (if applicable)
        g2.setFont(new Font("Verdana", Font.PLAIN, 18)); 
        for (int i = 0; i < 7; i++) {
            g2.drawString(Integer.toString(i*50), pad, (height - pad - (int)(i*scale*50)) + 5);

            // Draw dashed lines on graph if selected
            if (dashedBool && !scatterBool) {
            	drawDashedLine(g2, 2*pad, (height - pad - (int)(i*scale*50)), width - 2*pad, height - pad - (int)(i*scale*50));
                drawDashedLine(g2, (int) (2*pad) + (i*dateInc*2), getHeight() - pad, (int) (2*pad) + (i*dateInc*2), pad);
            }
        }

		// Y axis value label
		g2.setFont(new Font("Verdana", Font.BOLD, 16)); 
		affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(-90), 0, 0);
		g2.setFont(g2.getFont().deriveFont(affineTransform));
		g2.drawString("Value", pad/2 + 5, height/2 + 20);

		// Data points - iterate over all data points and mark data points with red ellipses
        inc = 0;
        initXPos = 0;
        initYPos = 0;
        listIt = sensorPoints.listIterator();
        listItFlagged = flaggedDataPoints.listIterator();
		g2.setStroke(new BasicStroke(1));

        // Scan linked list of sensor points
        while (listIt.hasNext()) {
            sensorPoint = listIt.next();
            flaggedPoint = listItFlagged.next();
        	
            // Check for fail - red = fail, blue = success
            if (flaggedPoint == 0)
            	g2.setPaint(Color.RED);
            else
            	g2.setPaint(Color.BLUE);

            xPos = (pad*2) + (inc*xInc);
            yPos = (height - pad - (scale*sensorPoint));

            if (scatterBool) {
            	g2.setFont(new Font("Verdana", Font.PLAIN, 7));
            	g2.drawString("x", (int) (xPos-2), (int) (yPos+2));

            	if (dashedBool && inc != 0)
        			g2.draw(new Line2D.Double(initXPos, initYPos, xPos, yPos));
            }
            else if (barBool) {
            	if (inc != 0 && inc != sensorPoints.size()) {
	            	distToXAxis = (int) (height - pad  - yPos);
	            	if (flaggedPoint == 0)
	            		g2.setPaint(Color.RED);
	            	else 
	            		g2.setPaint(new Color(31, 194, 226));

	        		g2.fillRect((int)(xPos-(barWidth-1)), (int)(yPos), barWidth, (int) (distToXAxis));
	        		g.setColor(Color.BLACK);
					g.drawRect((int)(xPos-(barWidth-1)), (int)(yPos), barWidth, (int) (distToXAxis));
	        	}
            }
            else
            	if (inc != 0)
        			g2.draw(new Line2D.Double(initXPos, initYPos, xPos, yPos));

			initXPos = xPos;
            initYPos = yPos;
            inc++;
        }

        // X axis labels - iterate over all date data points and mark on X axis
        g2.setFont(new Font("Verdana", Font.BOLD, 16)); 
        g2.setPaint(Color.BLACK);
        g2.drawString("Time",(width/2) - (pad/2), height-(pad/4));

        inc = 0;
        g2.setPaint(Color.BLACK);
        g2.setFont(new Font("default", Font.BOLD, 10));
        listItDates = datePoints.listIterator();
        while (listItDates.hasNext()) {
            datePoint = listItDates.next();
            g2.drawString(datePoint, (int) ((pad-20) + (inc*dateInc*2)), height - 20);
            inc++;
        }
    }

    /**
     * A simple method to draw dashed guidelines if the user selects the dashed line option when drawing the graph
     *
     * @param g The graphics instance to paint onto the UI.
     * @param x1 The first x position of where to begin drawing the line.
     * @param y1 The first y position of where to begin drawing the line.
     * @param x2 The second x position of where to finish drawing the line.
     * @param y2 The second y position of where to finish drawing the line.
     */
    public void drawDashedLine(Graphics g, double x1, double y1, double x2, double y2)
    {
        // Graphics2D instance
        g2d = (Graphics2D) g.create();
        g2d.setPaint(Color.BLACK);

        // Draw a basic dashed line
        dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        g2d.draw(new Line2D.Double(x1, y1, x2, y2));

        //gets rid of the copy
        g2d.dispose();
	}

    /**
     * Constructor to initialise sensor variables with relevant data for a selected device and initialise graph constants.
     *
     * @param sensorPoints The linked list of sensor data points to plot over time. 
     * @param datePoints The linked list of date strings to plot on the X axis.
     * @param flaggedDataPoints The linked list of flagged points to indicate if an error was picked up for this sensor value.
     * @param title_details The complete string to display at the top of each graph plot (title, sensor number etc.)
     * @param graphDetails The details to display for the graph underneath the title string.
     * @param dashedBool A boolean value to determine if the plot displays helper guidelines on the plot.
     * @param scatterBool A boolean value to determine if the graph should be displayed as a scatter graph.
     * @param barBool A boolean value to determine if the graph should be displayed as a bar graph.
     */
    public GraphComponent(LinkedList<Integer> sensorPoints, LinkedList<String> datePoints, LinkedList<Integer> flaggedDataPoints, String title_details, String graphDetails, boolean dashedBool, boolean scatterBool, boolean barBool)
    {
        this.sensorPoints = sensorPoints;
        this.datePoints = datePoints;
        this.flaggedDataPoints = flaggedDataPoints;
        this.title_details = title_details;
        this.graphDetails = graphDetails;
        this.dashedBool = dashedBool;
        this.scatterBool = scatterBool;
        this.barBool = barBool;
        
        this.barWidths = new int[] {28, 14, 5};
        this.pad = 40;
    }
}