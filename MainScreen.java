import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainScreen extends JPanel
{
	JButton button = new JButton("Button");
    JTabbedPane tabPane = new JTabbedPane();

    public MainScreen(){
        JPanel mainPanel = new JPanel();
        JPanel optionPanel = new JPanel();

	    GraphComponent graph = new GraphComponent();
		mainPanel.add(graph);

        tabPane.add("Home", mainPanel);
        tabPane.add("Options", optionPanel);
        tabPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        setLayout(new BorderLayout());
        add(tabPane, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);

    }

    public void displayScreen() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JFrame window = new JFrame();
                window.add(new MainScreen());

		        window.setTitle("Sensor Data Visualisation");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setLocationByPlatform(true);
                window.setSize(700, 700);
                window.setResizable(false);
                window.setVisible(true);

            }
        });
    }
}