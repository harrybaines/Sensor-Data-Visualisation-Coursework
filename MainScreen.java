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
    private JPanel homePanel;
    private JPanel topHomePanel;
    private JPanel midHomePanel;
    private JPanel botHomePanel;
    private JPanel optionPanel;
    private JPanel topOptPanel;
    private JPanel midOptPanel;
    private JPanel botOptPanel;

    // UI Components
    // Home Panel
    private JLabel addressLbl;
    private GraphComponent graph;
    private JTextField addressEntry;
    private JButton button;

    // Option panel
    private JButton openFileBtn;

    /**
     * Constructor to initialise panels and components on the UI.
     */
    public MainScreen()
    {
		// Window, panes and panels
    	window = new JFrame();
    	tabPane = new JTabbedPane();

        // Home panels
        homePanel = new JPanel(new BorderLayout());
        topHomePanel = new JPanel(new GridLayout(2,1));
        midHomePanel = new JPanel(new GridLayout(2,2));
        botHomePanel = new JPanel(new GridLayout(1,1));

        // Add panels to home  
        homePanel.add("North", topHomePanel);
        homePanel.add("Center", midHomePanel);
        homePanel.add("South", botHomePanel);

        // Option panels
        optionPanel = new JPanel(new BorderLayout());
        topOptPanel = new JPanel(new GridLayout(1,1));
        midOptPanel = new JPanel(new GridLayout(1,1));
        botOptPanel = new JPanel(new GridLayout(1,1));

        // Add panels to option
        optionPanel.add("North", topOptPanel);
        optionPanel.add("Center", midOptPanel);
        optionPanel.add("South", botOptPanel);

        // Components
        addressLbl = new JLabel("Search for device (address): ");
        addressLbl.setHorizontalAlignment(SwingConstants.CENTER);
        addressLbl.setFont(new Font("Helvetica", Font.BOLD, 20));
        addressLbl.setForeground(Color.RED);
        topHomePanel.add(addressLbl);

        addressEntry = new JTextField(20);
        addressEntry.addActionListener(this);
        topHomePanel.add(addressEntry);

        graph = new GraphComponent();
        midHomePanel.add(graph);

        addressEntry = new JTextField();
        button = new JButton("Button");
        openFileBtn = new JButton("Open File");
        openFileBtn.addActionListener(this);
        topOptPanel.add(openFileBtn);

        // Tab pane
        tabPane.add("Home", homePanel);
        tabPane.add("Options", optionPanel);
        tabPane.setBorder(new EmptyBorder(10, 10, 10, 10));
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