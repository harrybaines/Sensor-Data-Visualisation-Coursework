import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

/**
 * This class provides a simple, high-fidelity UI displaying sensor data visualisations.
 * Multiple tabs allow user to view multiple data visualisations with ease.
 *
 * @author Harry Baines
 */
public class MainScreen extends JPanel implements ActionListener
{
	// SensorData instance to obtain data lines from a CSV file
	SensorData data = new SensorData();

    // Used to store all devices found when a search has occurred
    private LinkedList<DataLine> devicesFound = new LinkedList<DataLine>();

	// Window and panels
	private JFrame window;
    private JTabbedPane tabPane;
    private JPanel homePanel;
    private JPanel topHomePanel;
    private JPanel midHomePanel;
    private JPanel botHomePanel;
    private JPanel sensorPanel;
    private JPanel topSensPanel;
    private JPanel midSensPanel;
    private JPanel botSensPanel;
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

    // Sensors Panel
    private JButton searchSensBut;
    private String[] columnNames = {"Time", "Type", "Version", "Counter", "Via", "Address", "Status", "Sensor Data"};
    
    private LinkedList<Object> tableData = new LinkedList<Object>();

    private DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    private JTable table = new JTable(tableModel);

    private ListIterator<DataLine> listIt;
    private DataLine deviceToAdd; 
    private JScrollPane scrollPane;

    // Options panel
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

        // Sensor Panels
        sensorPanel = new JPanel(new BorderLayout());
        topSensPanel = new JPanel(new GridLayout(3,1));
        midSensPanel = new JPanel(new GridLayout(4,2));
        botSensPanel = new JPanel(new GridLayout());

        // Add panels to sensors  
        sensorPanel.add("North", topSensPanel);
        sensorPanel.add("Center", midSensPanel);
        sensorPanel.add("South", botSensPanel);

        // Option panels
        optionPanel = new JPanel(new BorderLayout());
        topOptPanel = new JPanel(new GridLayout(1,1));
        midOptPanel = new JPanel(new GridLayout(1,1));
        botOptPanel = new JPanel(new GridLayout(1,1));

        // Add panels to options
        optionPanel.add("North", topOptPanel);
        optionPanel.add("Center", midOptPanel);
        optionPanel.add("South", botOptPanel);

        // Components - Home
        graph = new GraphComponent();
        midHomePanel.add(graph);

        addressEntry = new JTextField();
        button = new JButton("Button");

        // Components - Sensors
        addressLbl = new JLabel("Search for device (address): ");
        addressLbl.setHorizontalAlignment(SwingConstants.CENTER);
        addressLbl.setFont(new Font("Helvetica", Font.BOLD, 20));
        addressLbl.setForeground(Color.RED);
        topSensPanel.add(addressLbl);

        addressEntry = new JTextField(20);
        addressEntry.addActionListener(this);
        topSensPanel.add(addressEntry);

        searchSensBut = new JButton("Find");
        searchSensBut.addActionListener(this);
        topSensPanel.add(searchSensBut);

        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        midSensPanel.add("Center", scrollPane);

        // Components - Options
        openFileBtn = new JButton("Open File");
        openFileBtn.addActionListener(this);
        topOptPanel.add(openFileBtn);

        // Tab pane
        tabPane.add("Home", homePanel);
        tabPane.add("Sensors", sensorPanel);
        tabPane.add("Options", optionPanel);
        tabPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        add(tabPane, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
    }

    /**
     * A method to display the UI to the user.
     */
    public void displayScreen() 
    {
        SwingUtilities.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                window = new JFrame();
                window.add(new MainScreen());
		        window.setTitle("Sensor Data Visualisation");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setLocation(100,100);
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
        else if (e.getSource() == searchSensBut)
        {
        	// Clear table contents
        	DefaultTableModel dm = (DefaultTableModel)table.getModel();
			dm.getDataVector().removeAllElements();
			dm.fireTableDataChanged(); // notifies the JTable that the model has changed

            // Obtain linked list of devices found using user entry
            devicesFound = data.findDeviceByAddress(addressEntry.getText());

            // Iterate over linked list
            listIt = devicesFound.listIterator();

            // Add to output
            while (listIt.hasNext())
            {
            	// Obtain next device properties
                deviceToAdd = listIt.next();

         		// Store properties from data line
                Object[] dataToAdd = 
                {
                	deviceToAdd.getTime(), 
                	deviceToAdd.getType(), 
                	deviceToAdd.getVersion(), 
                	deviceToAdd.getCounter(), 
                	deviceToAdd.getVia(),
                	deviceToAdd.getAddress(), 
                	deviceToAdd.getStatus(), 
                	deviceToAdd.getSensorData(), 
                	deviceToAdd.getDateObtained()
                };

                // Add row to table
                tableModel.addRow(dataToAdd);
            }
            addressEntry.setText("");
        }
    }
}

