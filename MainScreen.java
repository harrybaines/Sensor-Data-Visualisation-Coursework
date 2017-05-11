import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.Collator;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

/**
 * This class provides a simple, high-fidelity UI displaying sensor data visualisations.
 * Multiple tabs allow user to view multiple data visualisations with ease.
 *
 * @author Harry Baines
 */
public class MainScreen extends JPanel implements ActionListener, ListSelectionListener
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

    private JPanel statsPanel;
    private JPanel topStatPanel;
    private JPanel midStatPanel;
    private JPanel botStatPanel;

    // UI Components
    // HOME panel components
    private GridBagConstraints c;
    private JLabel titleLbl;
    private JLabel versionLbl;
    private JLabel fileOpenedLbl;
    private int[] barGraphValues = {0,0,0};
    private JButton openFileBtn;
    private JButton exportBtn;
    private JButton quitBtn;

    // SENSORS panel components
    private JLabel addressLbl;
    private JTextField addressEntry;
    private JButton searchSensBut;
    private JLabel resultsFoundLbl;
    private String selectedItem;

    // Table variables
    private final String[] columnNames = {"Date Obtained", "Device Type", "Version", "Counter", "Via", "Device Address", "Status", "Sensor Data"};
    private final int[] columnWidths = {150,40,20,60,10,60,20,170};
    private LinkedList<Object> tableData = new LinkedList<Object>();
    private JTable table;
    private TableColumnModel columnModel;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private ListIterator<DataLine> listIt;
    private DataLine deviceToAdd; 

    // Info pop up panel variables
    private JPanel mainInfoPanel;
    private JPanel topInfoPanel;
    private JPanel midInfoPanel;
    private JLabel infoTitle;
    private JTable infoTable;
    private DefaultTableModel infoTableModel;
    private final String[] infoNames = {"Sensor Number", "Data (Decimal)", "Data (Hex)"};
    private int sensorDecValue;
    private int sensorHexValue;

    // Sort panel components
    private JLabel sortLbl;
    private String[] sorts = {"Time since last seen (DESC)", "Time since last seen (ASC)", "Number of errors found", "Messages missed by receiver"};
    private JComboBox<String> sortOpts = new JComboBox<String>(sorts);
    private JButton applySortBtn;

    // Visualise panel components
    private JLabel visualiseAsLbl;
    private String[] visuals = {"Sensor-Value-Over-Time Line Graph", "Sensor-Value-Over-Time Line Graph (DASHED)", "Scatter Graph", "Timeline"};
    private JComboBox<String> visOpts = new JComboBox<String>(visuals);
    private JButton applyVisBtn;
    private String[] plots = {"Plot All Sensor Values", "High-Frequency Plot", "Mid-Frequency Plot", "Low Frequency Plot"};
    private JComboBox<String> plotOpts = new JComboBox<String>(plots);
    private int[][] frequencyPlotValues = {{0,0}, {250,50}, {100,20}, {50,10}};

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

    // STATISTICS panel components
    private JLabel statsLbl; 
    private int[] errorsArray;

    /**
     * Constructor to initialise panels and place components on the UI.
     */
    public MainScreen()
    {
        // HOME panels
        homePanel = new JPanel(new BorderLayout());
        topHomePanel = new JPanel(new GridBagLayout());
        midHomePanel = new JPanel(new GridBagLayout());
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
        openFileBtn = new JButton("Open CSV File");
        openFileBtn.addActionListener(this);
        c.ipady = 50;
        c.ipadx = 80;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.insets = new Insets(20,20,20,20);
        midHomePanel.add(openFileBtn, c);

        // Home panel - bottom
        quitBtn = new JButton("Quit");
        quitBtn.addActionListener(this);
        c.gridx = 0;
        botHomePanel.add(quitBtn, c);

        // SENSORS panels
        sensorPanel = new JPanel(new BorderLayout());
        topSensPanel = new JPanel(new GridBagLayout());
        midSensPanel = new JPanel(new BorderLayout());
        botSensPanel = new JPanel(new BorderLayout());

        // Sensors panel - bottom
        botSortPanel = new JPanel(new GridLayout(4,1,10,10));
        botVisPanel = new JPanel(new GridLayout(5,1,20,20));
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
        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.getSelectionModel().addListSelectionListener(this);
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

        applyVisBtn = new JButton("Apply");
        applyVisBtn.addActionListener(this);

        JLabel plotOptLbl = new JLabel("Choose Graph Detail:");
        plotOptLbl.setHorizontalAlignment(SwingConstants.CENTER);
        plotOptLbl.setFont(new Font("Helvetica", Font.BOLD, 15));
        plotOptLbl.setForeground(Color.RED);

        botVisPanel.add(visualiseAsLbl);
        botVisPanel.add(visOpts);
        botVisPanel.add(plotOptLbl);
        botVisPanel.add(plotOpts);
        botVisPanel.add(applyVisBtn);

        // STATISTICS panels
        statsPanel = new JPanel(new BorderLayout());
        topStatPanel = new JPanel(new GridBagLayout());
        midStatPanel = new JPanel(new GridLayout(6,1));
        botStatPanel = new JPanel(new GridBagLayout());

        statsPanel.add("North", topStatPanel);
        statsPanel.add("Center", midStatPanel);
        statsPanel.add("South", botStatPanel);

        statsLbl = new JLabel("Data Statistics:");
        statsLbl.setHorizontalAlignment(SwingConstants.CENTER);
        statsLbl.setFont(new Font("Helvetica", Font.BOLD, 24));
        statsLbl.setForeground(Color.RED);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10,0,10,0);
        topStatPanel.add(statsLbl, c);

        midStatPanel.add(new BarGraphComponent(barGraphValues, "Error Statistics"));
        midStatPanel.add(new BarGraphComponent(barGraphValues, "Messages Missed By Receiver Plot"));

        exportBtn = new JButton("Save Graphs To File");
        exportBtn.addActionListener(this);
        c.gridwidth = 3;
        c.insets = new Insets(10,0,10,0);
        botStatPanel.add(exportBtn, c);

        // Tab pane
        tabPane = new JTabbedPane();
        tabPane.add("Home", homePanel);
        tabPane.add("Sensors", sensorPanel);
        tabPane.add("Data Statistics", statsPanel);
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
                window.setResizable(true);
                window.setVisible(true);
            }
        });
    }

    /**
     * A method to retrieve the selected table row.
     * A modal pop up window will appear displaying all in-depth details about this particular row.
     *
     * @param e The list selection listener instance.
     */
    public void valueChanged(ListSelectionEvent e)
    {   
        // Ensure only triggered once!
        if (!e.getValueIsAdjusting())
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    displayInfoScreen();
                }
            });
        }
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
                errorsArray = data.findNoOfErrors();
                barGraphValues[0] = data.getNoOfRecords();
                barGraphValues[1] = errorsArray[0];
                barGraphValues[2] = errorsArray[1];
                populateTableData();
            }
        }

        else if (e.getSource() == quitBtn)
        {
            System.exit(0);
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
            if (devicesFound.size() == 0)
                JOptionPane.showMessageDialog(new JFrame(), "Error - no data to sort! Please search for a device first.", "Error", JOptionPane.ERROR_MESSAGE);   
            else
                populateTableData();
        }

        // Visualise data as graphs
        else if (e.getSource() == applyVisBtn)
        {         
            // Retrieve user input for graph option and date ranges
            selectedItem = visOpts.getSelectedItem().toString();

            // Ensure data is in ordered ascending form
            data.sortData(sorts[1], devicesFound, sorts);

            if (devicesFound.size() == 0)
                JOptionPane.showMessageDialog(new JFrame(), "Error - no data to visualise! Please search for a device first.", "Error", JOptionPane.ERROR_MESSAGE);   
            else
            {
                // Schedule a job for the event-dispatching thread: creating + showing the graph UI.
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        displayGraphs();
                    }
                }); 
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

        // Check if devices found has fewer than 6 dates, if so use all dates to fill up space
        if (devicesFound.size() < 6)
            JOptionPane.showMessageDialog(new JFrame(), "Insufficient data to plot. 6 or more devices are required to plot the graphs.", "Error", JOptionPane.ERROR_MESSAGE);   
        else
        {   if (devicesFound.size() > 50 && visOpts.getSelectedItem().equals(visuals[2]) && plotOpts.getSelectedItem().equals(plots[0]))
                JOptionPane.showMessageDialog(new JFrame(), "Error - scatter graph could not be plotted: too much sensor data.", "Error", JOptionPane.ERROR_MESSAGE);   
            else
            {
                sensInc = 1;

                // Used to obtain index in frequency array - then will be used to access int values for plot
                frequencyPlotValues[0][0] = devicesFound.size();
                frequencyPlotValues[0][1] = devicesFound.size()/5;
                int index = -1;
                for (int i=0;i<plots.length;i++) {
                    if (plots[i].equals(plotOpts.getSelectedItem())) {
                        index = i;
                        break;
                    }
                }

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
                    double increment = (devicesFound.size()/frequencyPlotValues[index][0]);
                    double runningIncrement = 0;
                    int dateInc = frequencyPlotValues[index][1] - 1;

                    // Add occasional date between data points - used to display on graph component
                    while (listIt.hasNext())
                    {
                        deviceToCheck = listIt.next();
     
                        if (deviceCounter == runningIncrement && sensorPoints.size() < frequencyPlotValues[index][0])
                        { 
                            runningIncrement += increment;

                            try 
                            {
                                addSensorPoint(deviceToCheck, sensInc);
                            }
                            catch (NumberFormatException ex)
                            {
                                System.out.println("CANT CONVERT TO DECIMAL FROM HEX");
                            }
                            catch (StringIndexOutOfBoundsException str)
                            {
                                System.out.println("OUT OF BOUNDS!");
                            }
                            dateInc++;

                            // For every X data plots, write date string on X axis
                            if (dateInc == frequencyPlotValues[index][1])
                            {
                                addDatePoint(deviceToCheck);
                                dateInc = 0;
                            }
                        }

                        deviceCounter++;
                    }

                    // Add final sensor and date point values after while loop has finished
                    try
                    {
                        addSensorPoint(deviceToCheck, sensInc);
                        addDatePoint(deviceToCheck);
                    }
                    catch (NumberFormatException ex)
                    {
                        System.out.println("CANT CONVERT TO DECIMAL FROM HEX");
                    }
                    catch (StringIndexOutOfBoundsException str)
                    {
                        System.out.println("OUT OF BOUNDS!");
                    }

                    // Display error message detailing data that couldn't be plotted
                    if (sensorPoints.size() == 0)
                    {
                        JLabel errorLbl = new JLabel("Data could not be plotted for this sensor. See table below for details:");
                        errorLbl.setHorizontalAlignment(SwingConstants.CENTER);
                        errorLbl.setFont(new Font("Helvetica", Font.BOLD, 15));
                        errorLbl.setForeground(Color.RED);
                        graphPanels[i-1].add("Center", errorLbl);
                        graphTabPane.add(sensorString, graphPanels[i-1]);
                    }
                    else
                    {
                       // Prepare title string for graph plotting
                        title_details = ("Sensor " + i + " - Device Address " + deviceToCheck.getAddress());

                        // Add new graph type component to new panel
                        if (visOpts.getSelectedItem().equals("Sensor-Value-Over-Time Line Graph"))
                            graphPanels[i-1].add("Center", new GraphComponent(sensorPoints, datePoints, title_details, false, false));

                        else if (visOpts.getSelectedItem().equals("Sensor-Value-Over-Time Line Graph (DASHED)"))
                            graphPanels[i-1].add("Center", new GraphComponent(sensorPoints, datePoints, title_details, true, false));

                        else if (visOpts.getSelectedItem().equals("Scatter Graph"))
                            graphPanels[i-1].add("Center", new GraphComponent(sensorPoints, datePoints, title_details, false, true));

                        else if (visOpts.getSelectedItem().equals("Timeline"))
                            graphPanels[i-1].add("Center", new GraphComponent(sensorPoints, datePoints, title_details, false, true));

                        // Add new panel to tab pane 
                        graphTabPane.add(sensorString, graphPanels[i-1]);

                        // Add new export button to each panel
                        exportBtns[i-1] = new JButton("Save To File");
                        exportBtns[i-1].addActionListener(this);
                        graphPanels[i-1].add("South", exportBtns[i-1]); 
                    }

                    // Increase sensor value to extract particular points from data string
                    sensInc += 2;
                }

                // Further graph window details
                graphWindow.add(graphTabPane);
                graphWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                graphWindow.setSize(1600,700);
                graphWindow.setLocation(200,200);
                graphWindow.setVisible(true);
                graphWindow.setResizable(false); 
            } 
        }
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
                deviceToAdd.getDateObtained(),
                deviceToAdd.getType(), 
                deviceToAdd.getVersion(), 
                Integer.parseInt(deviceToAdd.getCounter(), 16),
                deviceToAdd.getVia(),
                deviceToAdd.getAddress(), 
                deviceToAdd.getStatus(), 
                deviceToAdd.getSensorData(), 
            };
                
            // Add row to table
            tableModel.addRow(dataToAdd);
        }
   
        // Results found data
        if (devicesFound.size() == 0)
            resultsFoundLbl.setText("No Results Found");
        else
            resultsFoundLbl.setText("Results Found: " + devicesFound.size());
    }

    private void addSensorPoint(DataLine deviceToCheck, int sensInc)
    {
        sensorValue = Integer.parseInt(deviceToCheck.getSensorData().substring(sensInc-1,sensInc+1), 16);
        sensorPoints.add(sensorValue);
    }

    private void addDatePoint(DataLine deviceToCheck)
    {
        dateValue = deviceToCheck.getDateObtained();
        datePoints.add(dateValue);
    }

    /**
     * Method to display a simple pop up window displaying all the details about the user selected row.
     * The user selects a row from the device table and the window appears.
     */
    private void displayInfoScreen()
    {
        window = new JFrame();
        mainInfoPanel = new JPanel(new BorderLayout());
        topInfoPanel = new JPanel();
        midInfoPanel = new JPanel(new GridLayout(8,2));
        JPanel botInfoPanel = new JPanel(new BorderLayout());

        infoTitle = new JLabel("Data Information");
        infoTitle.setHorizontalAlignment(SwingConstants.CENTER);
        infoTitle.setFont(new Font("Helvetica", Font.BOLD, 24));
        infoTitle.setForeground(Color.RED);
        topInfoPanel.add(infoTitle);

        JLabel deviceAddressLbl = new JLabel("Device Address: ");
        JLabel deviceTypeLbl = new JLabel("Device Type: ");
        JLabel deviceVersionLbl = new JLabel("Device version: ");
        JLabel errorsMissedLbl = new JLabel("Errors missed by Receiver (current total): ");
        JLabel viaLbl = new JLabel("Receiver that picked up this Transmission: ");
        JLabel statusLbl = new JLabel("Device Status: ");
        JLabel dateLbl = new JLabel("Date Obtained: ");
        JLabel dataLbl = new JLabel("Sensor Data:");

        midInfoPanel.add(deviceAddressLbl);
        midInfoPanel.add(deviceTypeLbl);
        midInfoPanel.add(deviceVersionLbl);
        midInfoPanel.add(errorsMissedLbl);
        midInfoPanel.add(viaLbl);
        midInfoPanel.add(statusLbl);
        midInfoPanel.add(dateLbl);
        midInfoPanel.add(dataLbl);

        // Clear table contents
        infoTableModel = new DefaultTableModel(infoNames, 0);
        infoTable = new JTable(infoTableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        infoTableModel = (DefaultTableModel) infoTable.getModel();
        infoTableModel.getDataVector().removeAllElements();
        infoTableModel.fireTableDataChanged();
        infoTable.setRowHeight(20);

        populateInfoData(table.getValueAt(table.getSelectedRow(), 7).toString());

        scrollPane = new JScrollPane(infoTable);
        botInfoPanel.add("Center", scrollPane);

        mainInfoPanel.add("North", topInfoPanel);
        mainInfoPanel.add("Center", midInfoPanel);
        mainInfoPanel.add("South", botInfoPanel);

        window.add(mainInfoPanel);
        window.setTitle("Sensor Data Information");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setLocation(100,100);
        window.setSize(600, 700);
        window.setResizable(false);
        window.setVisible(true);
    }

    /**
     * Method to insert all relevant sensor data into the info table on the info pop up window.
     * @param dataString The sensor data string to break down and display to the user.
     */
    private void populateInfoData(String dataString)
    {

        for (int i = 1; i <= dataString.length()/2; i++)
        {
            Object[] dataToAdd = 
            {
                Integer.toString(i),
                sensorDecValue = Integer.parseInt(dataString.substring(i-1,i+1), 16),
                sensorHexValue = Integer.parseInt(dataString.substring(i-1,i+1))
            };
            infoTableModel.addRow(dataToAdd);
        }
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
            JOptionPane.showMessageDialog(new JFrame(), "No File Saved.", "Info", JOptionPane.PLAIN_MESSAGE);   
        }
    }   
}



