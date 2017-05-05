import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.Collator;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
    private GridBagConstraints c;
    private JLabel titleLbl;
    private JLabel versionLbl;
    private JLabel fileOpenedLbl;
    private int[] barGraphValues = {1000,10,4,5};
    private JButton openFileBtn;
    private JButton exportBtn;

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
    private String[] sorts = {"Time since last seen (DESC)", "Time since last seen (ASC)", "Number of errors found", "Messages missed by receiver"};
    private JComboBox<String> sortOpts = new JComboBox<String>(sorts);
    private JButton applySortBtn;

    // Visualise panel components
    private JLabel visualiseAsLbl;
    private String[] visuals = {"Timeline", "Sensor-Value-Over-Time Line Graph", "Bar Chart", "Scatter Graph"};
    private JComboBox<String> visOpts = new JComboBox<String>(visuals);
    private JButton applyVisBtn;
    private String[] datesOne = {"No Data Selected"};
    private String[] datesTwo = {"No Data Selected"};
    private JComboBox<String> dateOptsOne = new JComboBox<String>(datesOne);
    private JComboBox<String> dateOptsTwo = new JComboBox<String>(datesTwo);
    private String dateOneOpt;
    private String dateTwoOpt;

    // Graph Dialog Visualisation Window Variables
    private JFrame graphWindow;
    private JPanel graphPanel;
    private LinkedList<Integer> sensorPoints;
    private LinkedList<String> datePoints;
    private String title_details;
    private int sensorValue;
    private String dateValue;
    private LinkedList<DataLine> flaggedDataLines = new LinkedList<DataLine>();
    private DataLine deviceToCheck;
    private JTabbedPane graphTabPane;
    private int sensInc;
    private String sensorString;
    private JButton[] exportBtns = new JButton[10];
    private JPanel[] graphPanels = new JPanel[10];
    private JFileChooser selectDest;
    private BufferedImage img;

    // OPTIONS panel components

    /**
     * Constructor to initialise panels and place components on the UI.
     */
    public MainScreen()
    {
        // HOME panels
        homePanel = new JPanel(new BorderLayout());
        topHomePanel = new JPanel(new GridBagLayout());
        midHomePanel = new JPanel(new BorderLayout());
        botHomePanel = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();

        // Add panels to home  
        homePanel.add("North", topHomePanel);
        homePanel.add("Center", midHomePanel);
        homePanel.add("South", botHomePanel);

        // Components - Home
        titleLbl = new JLabel("Sensor Data Visualisation");
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        titleLbl.setFont(new Font("Helvetica", Font.BOLD, 26));
        titleLbl.setForeground(Color.BLUE);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(20,0,0,0);
		topHomePanel.add(titleLbl, c);

		versionLbl = new JLabel("v.1.0");
        versionLbl.setHorizontalAlignment(SwingConstants.CENTER);
        versionLbl.setFont(new Font("Helvetica", Font.BOLD, 16));
        versionLbl.setForeground(Color.BLACK);
        c.ipady = 5;
		c.gridy = 2;
		c.insets = new Insets(0,10,0,0);
		topHomePanel.add(versionLbl, c);

        fileOpenedLbl = new JLabel("No File Opened");
        fileOpenedLbl.setHorizontalAlignment(SwingConstants.CENTER);
        fileOpenedLbl.setFont(new Font("Helvetica", Font.BOLD, 20));
        fileOpenedLbl.setForeground(Color.BLACK);
        c.ipady = 5;
		c.gridy = 3;
		c.insets = new Insets(30,0,0,0);
		topHomePanel.add(fileOpenedLbl, c);

		// Home panel - middle
	    midHomePanel.add("Center", new BarGraphComponent(barGraphValues, "Data Statistics"));

       	// Home panel - bottom
        openFileBtn = new JButton("Open CSV File");
        openFileBtn.addActionListener(this);
		c.ipady = 20;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.insets= new Insets(10,10,10,10);
		botHomePanel.add(openFileBtn, c);

        exportBtn = new JButton("Save Graph To File");
        exportBtn.addActionListener(this);
        c.gridx = 1;
        botHomePanel.add(exportBtn, c);

        // SENSORS panels
        sensorPanel = new JPanel(new BorderLayout());
        topSensPanel = new JPanel(new GridBagLayout());
        midSensPanel = new JPanel(new BorderLayout());
        botSensPanel = new JPanel(new BorderLayout());

        // Sensors panel - bottom
        botSortPanel = new JPanel(new GridLayout(4,1,10,10));
        botVisPanel = new JPanel(new GridLayout(7,1,10,10));
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
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
		c.insets= new Insets(10,0,10,0);
        topSensPanel.add(addressLbl, c);

        addressEntry = new JTextField(20);
        addressEntry.addActionListener(this);
        addressEntry.setHorizontalAlignment(SwingConstants.CENTER);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.ipady = 5;
        c.gridx = 0;
        c.gridy = 1;
		c.insets = new Insets(10,250,10,250);
        topSensPanel.add(addressEntry, c);

        searchSensBut = new JButton("Find");
        searchSensBut.addActionListener(this);
        searchSensBut.setHorizontalAlignment(SwingConstants.CENTER);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        topSensPanel.add(searchSensBut, c);

		resultsFoundLbl = new JLabel("No Results Found");
		resultsFoundLbl.setFont(new Font("Helvetica", Font.BOLD, 16));
		resultsFoundLbl.setHorizontalAlignment(SwingConstants.CENTER);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
        topSensPanel.add(resultsFoundLbl, c);

        // Sensors panel - table components
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel) 
        {
            public boolean isCellEditable(int row, int column) 
            {
                return false;
            }
        };
        //table.setFillsViewportHeight(true);
        table.setRowHeight(20);

        // Set the column widths in the table
        columnModel = table.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++)
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
   
        scrollPane = new JScrollPane(table);
        midSensPanel.add("Center", scrollPane);

        // Sensors panel - sort section
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
        visualiseAsLbl.setFont(new Font("Helvetica", Font.BOLD, 18));
        visualiseAsLbl.setForeground(Color.RED);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 30;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;

        applyVisBtn = new JButton("Apply");
        applyVisBtn.addActionListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 30;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 1;

		JLabel dateFromLbl = new JLabel("Choose Date Range:");
		dateFromLbl.setHorizontalAlignment(SwingConstants.CENTER);
        dateFromLbl.setFont(new Font("Helvetica", Font.BOLD, 15));
        dateFromLbl.setForeground(Color.RED);

        JLabel toLbl = new JLabel("TO");
        toLbl.setHorizontalAlignment(SwingConstants.CENTER);
        toLbl.setFont(new Font("Helvetica", Font.BOLD, 13));
        toLbl.setForeground(Color.BLACK);

        botVisPanel.add(visualiseAsLbl);
        botVisPanel.add(visOpts);
        botVisPanel.add(dateFromLbl);
        botVisPanel.add(dateOptsOne);
        botVisPanel.add(toLbl);
        botVisPanel.add(dateOptsTwo);
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
        
        // Tab pane
        tabPane = new JTabbedPane();
        tabPane.add("Home", homePanel);
        tabPane.add("Sensors", sensorPanel);
        tabPane.add("Options", optionPanel);
        tabPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        add(tabPane, BorderLayout.CENTER);
    }

    /**
     * A method to display the UI to the user thorugh the event-dispatching thread.
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
     * @param e The action event instance.
     */
    public void actionPerformed(ActionEvent e)
    {
        // Open a CSV file
        if (e.getSource() == openFileBtn)
        {
            if (data.findFile())
            {
            	fileOpenedLbl.setText(data.getFileName());

	            // Create bar graph with statistics on data from chosen file
	            barGraphValues[0] = data.getNoOfRecords();
	            barGraphValues[1] = data.findNoOfErrors()[0];
	            barGraphValues[2] = data.findNoOfErrors()[0];
	            barGraphValues[3] = data.findNoOfErrors()[1];
	            midHomePanel.add("Center", new BarGraphComponent(barGraphValues, "Data Statistics"));
            }
        }

        // Search for device by address
        else if (e.getSource() == searchSensBut)
        {
        	// Re-populate table with data from user input device address and populate date comboboxes
        	populateTableData();
        }

        // Sort the data in the table and populate date comboboxes
        else if (e.getSource() == applySortBtn)
        {
		    populateTableData();
        }

        // Visualise data as graphs
        else if (e.getSource() == applyVisBtn)
        {         
            // Retrieve user input for graph option and date ranges
            selectedItem = visOpts.getSelectedItem().toString();
            dateOneOpt = dateOptsOne.getSelectedItem().toString();
            dateTwoOpt = dateOptsTwo.getSelectedItem().toString();

            // Ensure data is in ordered ascending form
            data.sortData(sorts[1], devicesFound, sorts);

            if (devicesFound.size() == 0)
                JOptionPane.showMessageDialog(new JFrame(), "Error - no data to visualise! Please search for a sensor first.", "Error", JOptionPane.ERROR_MESSAGE);   
            else
            {
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

        // Check for button export button click on home screen
        else if (e.getSource() == exportBtn)
        {
            saveToFile(midHomePanel);
        }

        // Check for export button click on graph panels
        else
        {
            for (int i = 0; i < exportBtns.length; i++)
                if (e.getSource() == exportBtns[i])
                    saveToFile(graphPanels[i]);
        }    
    }

    /**
     * A method to display the graph user interface.
     */
    private void displayGraphs()
    {
        graphWindow = new JFrame("Scatter Graphs");
        graphTabPane = new JTabbedPane();
        graphTabPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        graphWindow.setLayout(new BorderLayout());
        graphWindow.add(graphTabPane, BorderLayout.CENTER);

        sensInc = 1;

        // Plot all data graphs for all sensors
        for (int i = 1; i <= 10; i++)
        {
            // Retrieve sensor name and create new panel
            sensorString = "Sensor " + i;
            graphPanels[i-1] = new JPanel(new BorderLayout());

            // Iterate over devices found and extract individual sensor values into linked list
            listIt = devicesFound.listIterator();
            sensorPoints = new LinkedList<Integer>();
            datePoints = new LinkedList<String>();

	        int deviceCounter = 0;
	        int increment = (int) (devicesFound.size()/8);
	        int runningIncrement = increment;

            // Add occasional date - used to display on graph component
            while (listIt.hasNext())
            {
            	deviceToCheck = listIt.next();
            	deviceCounter++;

            	if (deviceCounter == runningIncrement || deviceCounter == 1)
	        	{
	        		runningIncrement += increment;

	        		sensorValue = Integer.parseInt(deviceToCheck.getSensorData().substring(sensInc-1,sensInc+1), 16);
                	sensorPoints.add(sensorValue);
                	dateValue = deviceToCheck.getDateObtained();
                    datePoints.add(dateValue);
	        	}
            }

            // Prepare title string for graph plotting
            title_details = ("Sensor " + i + " - Device Address " + deviceToCheck.getAddress());

            // Add new graph type component to new panel
            if (visOpts.getSelectedItem().equals("Scatter Graph"))
                graphPanels[i-1].add("Center", new ScatterGraphComponent(sensorPoints, datePoints, title_details));

            else if (visOpts.getSelectedItem().equals("Sensor-Value-Over-Time Line Graph"))
                graphPanels[i-1].add("Center", new LineGraphComponent(sensorPoints, datePoints, title_details));

            // Add new panel to tab pane 
            graphTabPane.add(sensorString, graphPanels[i-1]);
            graphWindow.add(graphTabPane);

            // Add new export button to each panel
            exportBtns[i-1] = new JButton("Save To File");
            exportBtns[i-1].addActionListener(this);
            graphPanels[i-1].add("South", exportBtns[i-1]);

            // Increase sensor value to extract particular points from data string
            sensInc += 2;
        }

        // Further graph window details
        graphWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        graphWindow.setSize(1600,700);
        graphWindow.setLocation(200,200);
        graphWindow.setVisible(true);
        graphWindow.setResizable(false); 
    }

    /**
     * Method to clear the current contents of the table and re-populate it with sorted/searched data.
     */
    private void populateTableData()
    {
		// Clear table contents
        tableModel = (DefaultTableModel) table.getModel();
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();

    	// If user has searched for data, display that, otherwise show all
        devicesFound = data.findDeviceByAddress(addressEntry.getText());

        // Sort the data in the table from user input
        data.sortData(sortOpts.getSelectedItem().toString(), devicesFound, sorts);

        // Iterate over linked list and add to output
        listIt = devicesFound.listIterator();

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

            // Add dates to comboxes
            dateOptsOne.addItem(deviceToAdd.getDateObtained());
            dateOptsTwo.addItem(deviceToAdd.getDateObtained());
    	}
   
        // Results found data
        if (devicesFound.size() == 0)
            resultsFoundLbl.setText("No Results Found");
        else
            resultsFoundLbl.setText("Results Found: " + devicesFound.size());
    }

    /**
     * Method to save painted components on a panel to a png file in the users chosen directory.
     * @param panel The panel that contains the components to be saved to a png file.
     */
    private void saveToFile(JPanel panel)
    {
        selectDest = new JFileChooser();
        selectDest.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        selectDest.showSaveDialog(null);

        img = new BufferedImage(panel.getWidth(), panel.getHeight()-40, BufferedImage.TYPE_INT_RGB);
        panel.paint(img.getGraphics());
        try {
            ImageIO.write(img, "png", new File(selectDest.getSelectedFile().getPath() + ".png"));
            JOptionPane.showMessageDialog(new JFrame(), "Graph saved successfully!", "Success", JOptionPane.PLAIN_MESSAGE);   
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}



