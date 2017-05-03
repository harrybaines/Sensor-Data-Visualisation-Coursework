import java.awt.*;
import javax.swing.*;

public class SimpleBarPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.white;
    private static final Color BAR_COLOR = Color.red;

    private int[] inputData;

    public SimpleBarPanel(final int[] inputData) {
        this.inputData = inputData;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        drawBars(g);
    }

    private void drawBars(final Graphics g) {
        int /*i,*/ OUTER_MARGIN = 20,
                WIDTH = 800 + 2 * OUTER_MARGIN,
                HEIGHT = 600 + 2 * OUTER_MARGIN;
                /*SPACE_BETWEEN_BARS = 10, SPACE_ON_TOP_BOTTOM = 25;*/

        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(BAR_COLOR);
        final int barWidth = 20;
        for (int itemIndex = 0; itemIndex < inputData.length; itemIndex++) {
            final int x = OUTER_MARGIN + 25 * itemIndex;
            final int barHeight = 10 * inputData[itemIndex];
            final int y = barHeight;
            g.fillRect(x, y, barWidth, barHeight);
        }
    }
}