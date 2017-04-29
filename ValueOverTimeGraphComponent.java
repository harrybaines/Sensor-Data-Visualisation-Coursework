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

		

		// Draw axis lines
		g.setColor(Color.BLACK);
		g.drawLine(40, 40, 40, getHeight()-40);
		g.drawLine(40, getHeight()-40, getWidth()-40, getHeight()-40);
		g.drawLine(40, 40, 35, 45);
		g.drawLine(40, 40, 45, 45);
		g.drawLine(getWidth()-40, getHeight()-40, getWidth()-45, getHeight()-45);
		g.drawLine(getWidth()-40, getHeight()-40, getWidth()-45, getHeight()-35);
		
		// Draw axis labels
		g.setColor(Color.BLACK);
		for (int i = 0; i < 7; i++)
			g.drawString(Integer.toString(i*50), 12, getHeight() - 40 - (i*50));

		for (int i = 0; i < 8; i++)
			g.drawString(Integer.toString((i+1)*100), (i*100) + 80, getHeight() - 20);

	}
}

