import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * This class provides a simple, high-fidelity UI displaying sensor data visualisations.
 * Multiple tabs allow user to view multiple data visualisations with ease.
 *
 * @author Harry Baines
 */
public class MainScreen extends JPanel implements ActionListener
{
	// SensorData instance to find files and read data
	SensorData data = new SensorData();

	// Window and panels
	private JFrame window;
    private JTabbedPane tabPane;
    private JPanel mainPanel;
    private JPanel optionPanel;

    // UI Components
    private GraphComponent graph;
    private JButton button;
    private JButton openFileBtn;

    /**
     * Constructor to initialise panels and components on the UI.
     */
    public MainScreen()
    {
		// Window, panes and panels
    	window = new JFrame();
    	tabPane = new JTabbedPane();
        mainPanel = new JPanel();
        optionPanel = new JPanel();

        tabPane.add("Home", mainPanel);
        tabPane.add("Options", optionPanel);
        tabPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Components
        graph = new GraphComponent();
        button = new JButton("Button");
        openFileBtn = new JButton("Open File");
        openFileBtn.addActionListener(this);

		mainPanel.add(graph);
		optionPanel.add(openFileBtn);

        setLayout(new BorderLayout());
        add(tabPane, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
    }

    /**
     * A method to display the UI to the user.
     */
    public void displayScreen() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                window = new JFrame();
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

    /**
     * Detects if a button on the UI has been pressed.
     * Allows the user to search for a CSV file.
     *
     * @param e the action event instance.
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == openFileBtn)
        {
            data.findFile();
        }
    }
}