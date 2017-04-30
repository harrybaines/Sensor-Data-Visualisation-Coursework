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
    private SensorData data = new SensorData();

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
    private JPanel botSortPanel;
    private JPanel botVisPanel;

    private JPanel optionPanel;
    private JPanel topOptPanel;
    private JPanel midOptPanel;
    private JPanel botOptPanel;

    // UI Components
    // HOME panel components
    private JButton button;

    // SENSORS panel components
    private JLabel addressLbl;
    private JTextField addressEntry;
    private JButton searchSensBut;
    private JLabel resultsFoundLbl;
    private String selectedItem;

    // Table variables
    private final String[] columnNames = {"Time (s)", "Type", "Version", "Counter", "Via", "Address", "Status", "Sensor Data", "Date Obtained"};
    private final int[] columnWidths = {60,20,20,20,10,60,20,150,170};
    private LinkedList<Object> tableData = new LinkedList<Object>();
    private JTable table;
    private TableColumnModel columnModel;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private ListIterator<DataLine> listIt;
    private DataLine deviceToAdd; 

    // Sort panel components
    private JLabel sortLbl;
    private String[] sorts = new String[] {"Time since last seen", "Number of errors found", "Messages missed by receiver"};
    private JComboBox<String> sortOpts = new JComboBox<String>(sorts);
    private JButton applySortBtn;

    // Visualise panel components
    private JLabel visualiseAsLbl;
    private String[] visuals = new String[] {"Timeline", "Sensor-Value-Over-Time Line Graph", "Bar Chart", "Scatter Graph"};
    private JComboBox<String> visOpts = new JComboBox<String>(visuals);
    private JButton applyVisBtn;

	// Graph Dialog Visualisation Window Variables
	private JFrame graphWindow;
	private JPanel graphPanel;
	private LinkedList<Integer> sensorPoints;
    private LinkedList<DataLine> flaggedDataLines = new LinkedList<DataLine>();
    private DataLine deviceToCheck;
    private JTabbedPane graphTabPane;
    private int sensInc;
    private String sensorString;
    private int sensorValue;

    // OPTIONS panel components
    private JButton openFileBtn;

    /**
     * Constructor to initialise panels and place components on the UI.
     */
    public MainScreen()
    {
        // HOME panels
        homePanel = new JPanel(new BorderLayout());
        topHomePanel = new JPanel(new GridLayout(2,1));
        midHomePanel = new JPanel(new GridLayout(2,2));
        botHomePanel = new JPanel(new GridLayout(1,1));

        // Add panels to home  
        homePanel.add("North", topHomePanel);
        homePanel.add("Center", midHomePanel);
        homePanel.add("South", botHomePanel);

        // Components - Home
        addressEntry = new JTextField();
        button = new JButton("Button");

        // SENSORS panels
        sensorPanel = new JPanel(new BorderLayout());
        topSensPanel = new JPanel();
        midSensPanel = new JPanel(new GridLayout(2,1));
        botSensPanel = new JPanel(new BorderLayout());

        // Sensors panel - bottom
        botSortPanel = new JPanel(new GridLayout(4,4));
        botVisPanel = new JPanel(new GridLayout(4,4));

        botSensPanel.add("West", botSortPanel);
        botSensPanel.add("East", botVisPanel);

        // Add panels to sensors  
        sensorPanel.add("North", topSensPanel);
        sensorPanel.add("Center", midSensPanel);
        sensorPanel.add("South", botSensPanel);

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

        resultsFoundLbl = new JLabel("No Results Found");
        topSensPanel.add(resultsFoundLbl);

        // Sensors panel - table components
        tableModel = new DefaultTableModel(columnNames, 0);

        table = new JTable(tableModel) 
        {
            public boolean isCellEditable(int row, int column) 
            {
                return false;
            }
        };
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);

        // Set the column widths in the table
        columnModel = table.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++)
        	columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
   
        scrollPane = new JScrollPane(table);
        midSensPanel.add("Center", scrollPane);

        // Sensors panel - sort panel
        sortLbl = new JLabel("Sort Sensors By:");
        sortLbl.setHorizontalAlignment(SwingConstants.CENTER);
        sortLbl.setFont(new Font("Helvetica", Font.BOLD, 15));
        sortLbl.setForeground(Color.RED);
        applySortBtn = new JButton("Apply");
        applySortBtn.addActionListener(this);

        botSortPanel.add(sortLbl);
        botSortPanel.add(sortOpts);
        botSortPanel.add(applySortBtn);

        // Sensors panel - visualise panel
        visualiseAsLbl = new JLabel("Visualise Sensor Data As: ");
        visualiseAsLbl.setHorizontalAlignment(SwingConstants.CENTER);
        visualiseAsLbl.setFont(new Font("Helvetica", Font.BOLD, 15));
        visualiseAsLbl.setForeground(Color.RED);
        applyVisBtn = new JButton("Apply");
        applyVisBtn.addActionListener(this);

        botVisPanel.add(visualiseAsLbl);
        botVisPanel.add(visOpts);
        botVisPanel.add(applyVisBtn);

        // OPTIONS panels
        optionPanel = new JPanel(new BorderLayout());
        topOptPanel = new JPanel(new GridLayout(1,1));
        midOptPanel = new JPanel(new GridLayout(1,1));
        botOptPanel = new JPanel(new GridLayout(1,1));

        // Add panels to options
        optionPanel.add("North", topOptPanel);
        optionPanel.add("Center", midOptPanel);
        optionPanel.add("South", botOptPanel);

        // Components - Options
        openFileBtn = new JButton("Open File");
        openFileBtn.addActionListener(this);
        topOptPanel.add(openFileBtn);

        // Tab pane
        tabPane = new JTabbedPane();
        tabPane.add("Home", homePanel);
        tabPane.add("Sensors", sensorPanel);
        tabPane.add("Options", optionPanel);
        tabPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        add(tabPane, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
    }

    /**
     * A method to display the UI to the user using the event-dispatching thread.
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
                window.setSize(900, 1000);
                window.setResizable(false);
                window.setVisible(true);
            }
        });
    }

    /**
     * Detects if a button on the UI has been pressed.
     * Allows the user to search for a sensor device, sort data in the table, visualise data as graphs and search for a CSV file.
     *
     * @param e the action event instance.
     */
    public void actionPerformed(ActionEvent e)
    {
    	// Open a CSV file
        if (e.getSource() == openFileBtn)
        {
            data.findFile();
        }

        // Search for device by address
        else if (e.getSource() == searchSensBut)
        {
            // Clear table contents
            tableModel = (DefaultTableModel) table.getModel();
            tableModel.getDataVector().removeAllElements();
            tableModel.fireTableDataChanged();

            // Obtain linked list of devices found using user entry
            devicesFound = data.findDeviceByAddress(addressEntry.getText());

            // Iterate over linked list and add to output
            listIt = devicesFound.listIterator();

            while (listIt.hasNext())
            {
                // Obtain next device properties
                deviceToAdd = listIt.next();

                // Store properties from data line
                Object[] dataToAdd = {
                	deviceToAdd.getTime(), 
                    deviceToAdd.getType(), 
                    deviceToAdd.getVersion(), 
                    deviceToAdd.getCounter(), 
                    deviceToAdd.getVia(),
                    deviceToAdd.getAddress(), 
                    deviceToAdd.getStatus(), 
                    deviceToAdd.getSensorData(), 
                    deviceToAdd.getDateObtained()};
                    
                // Add row to table
                tableModel.addRow(dataToAdd);
            }
            addressEntry.setText("");

            // Results found data
            if (devicesFound.size() == 0)
                resultsFoundLbl.setText("No Results Found");
            else
                resultsFoundLbl.setText("Results Found: " + devicesFound.size());
        }

        // Sort the data in the table
        else if (e.getSource() == applySortBtn)
        {
            System.out.println("SORT!");
        }

        // Visualise data as graphs
        else if (e.getSource() == applyVisBtn)
        {         
        	// Retrieve user input 
            selectedItem = visOpts.getSelectedItem().toString();
            if (selectedItem.equals("Scatter Graph") || selectedItem.equals("Sensor-Value-Over-Time Line Graph"))
            {
				// Schedule a job for the event-dispatching thread: creating + showing the graph UI.
                SwingUtilities.invokeLater(new Runnable() 
                {
                    @Override
                    public void run() 
                    {
                        displayGraphs();
                    }
                }); 
            }   
        }
    }

    /**
     * A method to display the graph user interface.
     */
    private void displayGraphs()
    {
    	// Graph window details
    	graphWindow = new JFrame("Scatter Graphs");
	    graphTabPane = new JTabbedPane();
	    graphTabPane.setBorder(new EmptyBorder(10, 10, 10, 10));
	    graphWindow.setLayout(new BorderLayout());
	    graphWindow.add(graphTabPane, BorderLayout.CENTER);

	    sensInc = 1;

	    // Plot all data graphs for all sensors
	    for (int i = 1; i <= 10; i++)
	    {
	    	// Retrieve sensor name
	        sensorString = "Sensor " + i;

	        graphPanel = new JPanel(new BorderLayout());

	        // IMPLEMENT LINKEDLIST OF DATE POINTS HERE, USE %i TO GET OCCASIONAL DATA INTO THIS STRUCTURE

	        // Iterate over devices found and extract individual sensor values into linked list
	        listIt = devicesFound.listIterator();
	        sensorPoints = new LinkedList<Integer>();

	        while (listIt.hasNext())
	        {
	            deviceToCheck = listIt.next();
	            sensorValue = Integer.parseInt(deviceToCheck.getSensorData().substring(sensInc-1,sensInc+1), 16);
	            sensorPoints.add(sensorValue);
	        }

	        // Add new graph type component to new panel
	        if (visOpts.getSelectedItem().equals("Scatter Graph"))
	            graphPanel.add("Center", new ScatterGraphComponent(sensorPoints));

	        else if (visOpts.getSelectedItem().equals("Sensor-Value-Over-Time Line Graph"))
	            graphPanel.add("Center", new LineGraphComponent(sensorPoints));

	        // Add new panel to tab pane 
	        graphTabPane.add(sensorString, graphPanel);
	        graphWindow.add(graphTabPane);

	        // Increase sensor value to extract particular points from data string
	        sensInc += 2;
	    }

	    // Further graph window details
	    graphWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    graphWindow.setSize(1400,700);
	    graphWindow.setLocation(200,200);
	    graphWindow.setVisible(true);
	    graphWindow.setResizable(false); 
    }
}
