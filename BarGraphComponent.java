import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

/**
 * A class to model a simple bar graph component.
 * The graph displays all the errors found in the sensor data and outputs a simple bar graph.
 *
 * @author Harry Baines
 */
public class BarGraphComponent extends JPanel 
{
    private int[] values;
    private final String[] xAxisNames = {"Records Found", "Errors Found", "Different Errors Found", "Different Receivers Found", "Unique Data Lines Found"};
    private String title;

    public BarGraphComponent(int[] v, String t) {
       values = v;
       title = t;
    }

    /**
     * Method to paint a scatter graph component on the UI.
     * @param g The graphics component instance to paint onto the UI.
     */
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);

        if (values == null || values.length == 0)
          return;
        double minValue = 0;
        double maxValue = 0;
        for (int i = 0; i < values.length; i++) {
          if (minValue > values[i])
            minValue = values[i];
          if (maxValue < values[i])
            maxValue = values[i];
        }


        Dimension d = getSize();
        //System.out.println(d);

        int clientWidth = d.width;
        int clientHeight = d.height;
        int barWidth = clientWidth / values.length;

        Font titleFont = new Font("SansSerif", Font.BOLD, 20);
        FontMetrics titleFontMetrics = g.getFontMetrics(titleFont);
        Font labelFont = new Font("SansSerif", Font.PLAIN, 10);
        FontMetrics labelFontMetrics = g.getFontMetrics(labelFont);

        int titleWidth = titleFontMetrics.stringWidth(title);
        int y = titleFontMetrics.getAscent();
        int x = (clientWidth - titleWidth)/2;
        g.setFont(titleFont);
        g.drawString(title, x, y);

        int top = titleFontMetrics.getHeight();
        int bottom = labelFontMetrics.getHeight();
        if (maxValue == minValue)
          return;
        double scale = (clientHeight - top - bottom) / (maxValue - minValue);
        y = clientHeight - labelFontMetrics.getDescent();
        g.setFont(labelFont);

        for (int i = 0; i < values.length; i++) {
          int valueX = i * barWidth + 1;
          int valueY = top;
          int height = (int) (values[i] * scale);
          if (values[i] >= 0)
            valueY += (int) ((maxValue - values[i]) * scale);
          else {
            valueY += (int) (maxValue * scale);
            height = -height;
          }

          g.setColor(Color.red);
          g.fillRect(valueX, valueY, barWidth - 2, height);
          g.setColor(Color.black);
          g.drawRect(valueX, valueY, barWidth - 2, height);
          int labelWidth = labelFontMetrics.stringWidth(xAxisNames[i]);
          x = i * barWidth + (barWidth - labelWidth) / 2;
          g.drawString(xAxisNames[i], x, y);
        }
    }
}