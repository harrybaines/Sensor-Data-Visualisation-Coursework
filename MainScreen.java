import java.awt.*;
import javax.swing.*;

public class MainScreen
{
	public MainScreen()
	{
		JFrame window = new JFrame();
		JPanel mainPanel = new JPanel();
		GraphComponent graph = new GraphComponent();

		mainPanel.add(graph);

		// further window details
        window.setContentPane(mainPanel);
        window.setTitle("Sensor Data Visualisation");
        window.setSize(700, 700);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocation(200,200);
        window.setResizable(false);
        window.setVisible(true);
	}
}