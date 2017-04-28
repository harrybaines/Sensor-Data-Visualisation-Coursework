import java.awt.*;
import java.util.*;

public class ValueOverTimeGraphComponent extends Canvas 
{
	private LinkedList<DataLine> dataLines = new LinkedList<DataLine>();
	private LinkedList<DataLine> flaggedDataLines = new LinkedList<DataLine>();
	private DataLine deviceToCheck;
    private ListIterator<DataLine> listIt;

    private int sensNo = 1;
    private int inc = 1;

	public ValueOverTimeGraphComponent(LinkedList<DataLine> dataLines) 
	{
		listIt = dataLines.listIterator();
		this.dataLines = dataLines;
		// Graph size
		setPreferredSize(new Dimension(850, 350));
		setVisible(true); 
	}

	// Override the paint method - allows use to draw elements
	@Override 
	public void paint(Graphics g) 
	{
		g.setPaintMode();

		// Clear entire canvas
		g.setColor(Color.WHITE);
		g.clearRect(0, 0, getWidth(), getHeight());

		// Draw a background with a 10 pixel border
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(10, 10, getWidth()-20, getHeight()-20);

		// Draw some data - random, yours should be real :)
		// g.setColor(Color.WHITE);
		// for (int x=30; x<getWidth()-20; x+=10) 
		// { 
		// 	g.drawLine(x, getHeight()-20, x, getHeight()-20-(int)(Math.random()*getHeight()*0.8)); 
		// }

		listIt = dataLines.listIterator();

        while (listIt.hasNext())
        {
        	deviceToCheck = listIt.next();

        	if (deviceToCheck.getSensorData().length() > 20)
        		flaggedDataLines.add(deviceToCheck);
        	else
        	{
        		sensNo = 1;
        		inc = 1;
	        	// Read 10 sensor values
	        	while (inc <= 19)
	        	{
	        		System.out.println("Sensor " + sensNo + ": " + Integer.parseInt(deviceToCheck.getSensorData().substring(inc-1,inc+1), 16));
	        		inc += 2;
	        		sensNo++;
	        	}
	        	System.out.println("\n");
        	}
        }

		g.setColor(Color.WHITE);

		// Draw axis lines
		g.setColor(Color.BLACK);
		g.drawLine(20, 20, 20, getHeight()-20);
		g.drawLine(20, getHeight()-20, getWidth()-20, getHeight()-20);
	}


}