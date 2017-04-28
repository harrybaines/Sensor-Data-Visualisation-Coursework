import java.awt.*;

public class GraphComponent extends Canvas 
{
	public GraphComponent() 
	{
		// graph size
		setPreferredSize(new Dimension(850, 350));
		setVisible(true); 
	}

	// override the paint method - allows use to draw elements
	@Override 
	public void paint(Graphics g) 
	{
		// clear entire canvas
		g.setColor(Color.WHITE);
		g.clearRect(0, 0, getWidth(), getHeight());

		// draw a background with a 10 pixel border
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(10, 10, getWidth()-20, getHeight()-20);

		// draw axis lines
		g.setColor(Color.BLACK);
		g.drawLine(20, 20, 20, getHeight()-20);
		g.drawLine(20, getHeight()-20, getWidth()-20, getHeight()-20);

		// draw some data - random, yours should be real :)
		for (int x=30; x<getWidth()-20; x+=10) 
		{ 
			g.drawLine(x, getHeight()-20, x, getHeight()-20-(int)(Math.random()*getHeight()*0.8)); 
		}
	}
}