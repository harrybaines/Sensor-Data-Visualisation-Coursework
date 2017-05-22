import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.Math;
import java.text.Collator;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
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
    private SensorData data;

    // Used to store all devices found when a search has occurred
    private LinkedList<DataLine> devicesFound;

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

    private JFrame graphWindow;
    private JPanel graphPanel;
    private JPanel[] graphPanels;

    private JFrame infoWindow;
    private JPanel mainInfoPanel;
    private JPanel topInfoPanel;
    private JPanel midInfoPanel;
    private JPanel botInfoPanel;

    private JPanel statsPanel;
    private JPanel topStatPanel;
    private JPanel midStatPanel;
    private JPanel botStatPanel;

    private JPanel eventsPanel;
    private JPanel topEventsPanel;
    private JPanel midEventsPanel;
    private JPanel botEventsPanel;

    // UI Components
    // HOME panel components
    private GridBagConstraints c;
    private JLabel titleLbl;
    private JLabel versionLbl;
    private JLabel fileOpenedLbl;
    private JLabel basicDataLbl;
    private JLabel noOfLinesLbl;
    private JLabel noOfErrorsLbl;
    private JLabel percentErrorLbl;
    private JLabel devicesFoundLbl;
    private JLabel linesLbl;
    private double errorValue;
    private JLabel errorsLbl;
    private JLabel percentLbl;
    private JLabel devicesFoundNoLbl;
    private JLabel firstDateReadingLbl;
    private JLabel firstDateLbl;
    private JLabel recentReadingDateLbl;
    private JLabel recentReadingLbl;
    private JButton openFileBtn;
    private JButton exportBtn;
    private JButton quitBtn;

    // SENSORS panel components
    private JLabel addressLbl;
    private JButton searchSensBut;
    private JLabel resultsFoundLbl;
    private JLabel plotOptLbl;
    private String selectedItem;
    private JComboBox<String> sensorOpts;

    // Table variables
    private final String[] columnNames;
    private final int[] columnWidths;
    private JTable table;
    private TableColumnModel columnModel;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private ListIterator<DataLine> listIt;
    private DataLine deviceToAdd; 

    // Info pop up panel variables
    private JTable infoTable;
    private DefaultTableModel infoTableModel;
    private final String[] infoNames;
    private int sensorDecValue;
    private String sensorHexValue;
    private JButton quitInfoBtn;
    private JLabel notesDetailsLbl;
    private JLabel infoTitle;
    private JLabel deviceAddressLbl;
    private JLabel deviceTypeLbl;
    private JLabel deviceVersionLbl;
    private JLabel errorsMissedLbl;
    private JLabel viaLbl;
    private JLabel statusLbl;
    private JLabel dateLbl;
    private JLabel dataLbl;
    private JLabel addressDetails;
    private JLabel typeDetails;
    private JLabel versionDetails;
    private JLabel errorsDetails;
    private JLabel viaDetails;
    private JLabel statusDetails;
    private JLabel dateDetails;
    private JLabel dataDetails;
    private JLabel generalNotesLbl;

    // Sort panel components
    private JLabel sortLbl;
    private String[] sorts;
    private JComboBox<String> sortOpts;
    private JButton applySortBtn;

    // Visualise panel components
    private JLabel visualiseAsLbl;
    private String[] visuals;
    private JComboBox<String> visOpts;
    private JButton applyVisBtn;
    private String[] plots;
    private JComboBox<String> plotOpts;
    private int[][] frequencyPlotValues;

    // Graph Dialog Visualisation Window Variables
    private LinkedList<Integer> sensorPoints;
    private LinkedList<String> datePoints;
    private LinkedList<Integer> flaggedDataPoints;
    private String title_details;
    private int sensorValue;
    private String dateValue;
    private DataLine deviceToCheck;
    private JTabbedPane graphTabPane;
    private int sensInc;
    private String sensorString;
    private JButton[] exportBtns;
    private JFileChooser selectDest;
    private BufferedImage img;
    private JLabel errorLbl;
    private int index;
    private int deviceCounter;
    private double increment;
    private double runningIncrement;
    private int dateInc;
    private String graphDetails;

    // STATISTICS panel components
    private JLabel statsLbl; 
    private JButton findStatsForDeviceBtn;
    private JComboBox<String> deviceStatOpts;
    private int[] errorStatValues;
    private int[] sensorMinStatValues;
    private int[] sensorMaxStatValues;
    private int[] sensorAvgStatValues;
    private int[] errorsArray;
    private JLabel statisticsFileLbl;
    private JLabel findDeviceLbl;

    // EVENTS panel components
    private JComboBox<String> sensorOptsEvents;
    private JComboBox<String> sensorNoOpts;
    private JButton findEventsForSensBtn;
    private JTable eventsTable;
    private final String[] eventNames;
    private JButton saveEventsToFileBtn;
    private LinkedList<String> eventsFound;
    private JLabel eventsTitleLbl;
    private JLabel viewSensorsFoundLbl;
    private JLabel deviationExplLbl;
    private JLabel sensorNameLbl;
    private JLabel sensorNoLbl;
    private Object[] noCsv;
    private LinkedList<DataLine> sensorEventsList;
    private LinkedList<DataLine> deviceEventsList;
    private ArrayList<Integer> sensorMeans;
    private boolean errorInConversion;
    private String deviationString;
    private ArrayList<Integer> deviationValues;
    private int deviationValue;
    private int sensorIndex;
    private Object[] dataToAdd;
    private JFileChooser chooser;
    private String filename;
    private ListIterator<String> eventsIt;
    private BufferedWriter file;   
    private String nextEvent;

    /**
     * Constructor to initialise panels and place components on the UI.
     */
    public MainScreen() 
    {
        // INSTANCE VARIABLES INITIALISATION
        data = new SensorData();
        devicesFound = new LinkedList<DataLine>();
        eventsFound = new LinkedList<String>();

        // String arrays for user options
        columnNames = new String[] {"Date Obtained", "Device Type", "Version", "Counter", "Via", "Device Address", "Status", "Sensor Data"};
        infoNames = new String[] {"Sensor Number", "Data (Decimal)", "Data (Hex)"};
        sorts = new String[] {"Time since last seen (DESC)", "Time since last seen (ASC)", "Status Codes", "Messages missed by receiver"};
        visuals = new String[] {"Sensor-Value-Over-Time Line Graph", "Sensor-Value-Over-Time Line Graph (DASHED)", "Scatter Graph", "Scatter Graph (JOINED)", "Bar Graph", "Bar Graph (DASHED)"};
        plots = new String[] {"Plot All Sensor Values", "Mid-Frequency Plot", "Low Frequency Plot"};
        eventNames = new String[] {"Event Summary For This Device"};

        // Statistics and graph variables and values
        frequencyPlotValues = new int[][] {{0,0}, {100,20}, {50,10}};
        errorStatValues = new int[] {0,0,0};
        sensorMinStatValues = new int[10];
        sensorMaxStatValues = new int[10];
        sensorAvgStatValues = new int[10];
        columnWidths = new int[] {150,40,20,60,10,60,20,170};

        // Combobox and panel/button components to place on the UI
        sensorOpts = new JComboBox<String>();
        sensorOptsEvents = new JComboBox<String>();
        sensorNoOpts = new JComboBox<String>();
        deviceStatOpts = new JComboBox<String>();
        sortOpts =  new JComboBox<String>(sorts);
        plotOpts = new JComboBox<String>(plots);
        visOpts = new JComboBox<String>(visuals);
        graphPanels = new JPanel[10];
        exportBtns = new JButton[10];

        // UI COMPONENTS
        // HOME panels
        homePanel = new JPanel(new BorderLayout());
        topHomePanel = new JPanel(new GridBagLayout());
        midHomePanel = new JPanel(new GridBagLayout());
        midHomePanel.setBorder(new EmptyBorder(10, 20, 10, 20));
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
        basicDataLbl = new JLabel("Data Summary:");
        basicDataLbl.setHorizontalAlignment(SwingConstants.CENTER);
        basicDataLbl.setFont(new Font("Helvetica", Font.BOLD, 24));
        basicDataLbl.setForeground(Color.BLUE);
        c.gridy = 4;
        c.insets = new Insets(100,0,0,0);
        topHomePanel.add(basicDataLbl, c);
        
        noOfLinesLbl = new JLabel("Number Of Lines");
        noOfLinesLbl.setHorizontalAlignment(SwingConstants.CENTER);
        noOfLinesLbl.setFont(new Font("Helvetica", Font.ITALIC, 20));
        noOfLinesLbl.setForeground(Color.BLUE);
        c.gridwidth = 1;
        c.gridheight = 2;
        c.ipady = 5;
        c.gridy = 0;
        c.gridx = 0;
        c.weighty = 0.1;
        c.weightx = 0.1;
        c.insets = new Insets(0,5,100,5);
        midHomePanel.add(noOfLinesLbl, c);

        noOfErrorsLbl = new JLabel("Number Of Errors");
        noOfErrorsLbl.setHorizontalAlignment(SwingConstants.CENTER);
        noOfErrorsLbl.setFont(new Font("Helvetica", Font.ITALIC, 20));
        noOfErrorsLbl.setForeground(Color.BLUE);
        c.gridx = 1;
        c.gridy = 0;
        midHomePanel.add(noOfErrorsLbl, c);

        percentErrorLbl = new JLabel("% Error");
        percentErrorLbl.setHorizontalAlignment(SwingConstants.CENTER);
        percentErrorLbl.setFont(new Font("Helvetica", Font.ITALIC, 20));
        percentErrorLbl.setForeground(Color.BLUE);
        c.gridx = 2;
        c.gridy = 0;
        midHomePanel.add(percentErrorLbl, c);

        devicesFoundLbl = new JLabel("Unique Devices Found");
        devicesFoundLbl.setHorizontalAlignment(SwingConstants.CENTER);
        devicesFoundLbl.setFont(new Font("Helvetica", Font.ITALIC, 20));
        devicesFoundLbl.setForeground(Color.BLUE);
        c.gridx = 0;        
        c.gridy = 2;
        midHomePanel.add(devicesFoundLbl, c);

        firstDateReadingLbl = new JLabel("First Reading");
        firstDateReadingLbl.setHorizontalAlignment(SwingConstants.CENTER);
        firstDateReadingLbl.setFont(new Font("Helvetica", Font.ITALIC, 20));
        firstDateReadingLbl.setForeground(Color.BLUE);
        c.gridx = 1;        
        c.gridy = 2;
        midHomePanel.add(firstDateReadingLbl, c);

        recentReadingDateLbl = new JLabel("Most Recent Reading");
        recentReadingDateLbl.setHorizontalAlignment(SwingConstants.CENTER);
        recentReadingDateLbl.setFont(new Font("Helvetica", Font.ITALIC, 20));
        recentReadingDateLbl.setForeground(Color.BLUE);
        c.gridx = 2;        
        c.gridy = 2;
        midHomePanel.add(recentReadingDateLbl, c);

        linesLbl = new JLabel("0");
        linesLbl.setHorizontalAlignment(SwingConstants.CENTER);
        linesLbl.setFont(new Font("Helvetica", Font.BOLD, 20));
        linesLbl.setForeground(Color.BLACK);
        c.gridx = 0;
        c.gridy = 1;        
        c.insets = new Insets(5,0,10,0);
        midHomePanel.add(linesLbl, c);

        errorsLbl = new JLabel("0");
        errorsLbl.setHorizontalAlignment(SwingConstants.CENTER);
        errorsLbl.setFont(new Font("Helvetica", Font.BOLD, 20));
        errorsLbl.setForeground(Color.BLACK);
        c.gridx = 1;        
        c.gridy = 1;
        midHomePanel.add(errorsLbl, c);

        percentLbl = new JLabel("0.00%");
        percentLbl.setHorizontalAlignment(SwingConstants.CENTER);
        percentLbl.setFont(new Font("Helvetica", Font.BOLD, 20));
        percentLbl.setForeground(Color.BLACK);
        c.gridx = 2;        
        c.gridy = 1;
        midHomePanel.add(percentLbl, c);

        devicesFoundNoLbl = new JLabel("0");
        devicesFoundNoLbl.setHorizontalAlignment(SwingConstants.CENTER);
        devicesFoundNoLbl.setFont(new Font("Helvetica", Font.BOLD, 20));
        devicesFoundNoLbl.setForeground(Color.BLACK);
        c.gridx = 0;        
        c.gridy = 3;
        midHomePanel.add(devicesFoundNoLbl, c);

        firstDateLbl = new JLabel("-");
        firstDateLbl.setHorizontalAlignment(SwingConstants.CENTER);
        firstDateLbl.setFont(new Font("Helvetica", Font.BOLD, 20));
        firstDateLbl.setForeground(Color.BLACK);
        c.gridx = 1;        
        c.gridy = 3;
        midHomePanel.add(firstDateLbl, c);

        recentReadingLbl = new JLabel("-");
        recentReadingLbl.setHorizontalAlignment(SwingConstants.CENTER);
        recentReadingLbl.setFont(new Font("Helvetica", Font.BOLD, 20));
        recentReadingLbl.setForeground(Color.BLACK);
        c.gridx = 2;        
        c.gridy = 3;
        midHomePanel.add(recentReadingLbl, c);

        // Home panel - bottom
        openFileBtn = new JButton("Open CSV File");
        openFileBtn.addActionListener(this);
        c.ipady = 50;
        c.ipadx = 80;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.insets = new Insets(20,20,20,20);
        botHomePanel.add(openFileBtn, c);

        quitBtn = new JButton("Quit");
        quitBtn.addActionListener(this);
        c.gridx = 1;
        botHomePanel.add(quitBtn, c);

        // SENSORS panels
        sensorPanel = new JPanel(new BorderLayout());
        topSensPanel = new JPanel(new GridBagLayout());
        midSensPanel = new JPanel(new BorderLayout(10,10));
        midSensPanel.setBorder(new EmptyBorder(40,20,20,20));
        botSensPanel = new JPanel(new BorderLayout());

        // Add panels to sensors  
        sensorPanel.add("North", topSensPanel);
        sensorPanel.add("Center", midSensPanel);
        sensorPanel.add("South", botSensPanel);

        // Components - Sensors
        addressLbl = new JLabel("Search For Device Data (By Address):");
        addressLbl.setHorizontalAlignment(SwingConstants.CENTER);
        addressLbl.setFont(new Font("Helvetica", Font.BOLD, 24));
        addressLbl.setForeground(new Color(9, 137, 11));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.insets= new Insets(10,0,50,0);
        topSensPanel.add(addressLbl, c);

        sensorOpts.addItem("<No File Opened>");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        //c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(40,250,5,250);
        topSensPanel.add(sensorOpts, c);

        searchSensBut = new JButton("Find");
        searchSensBut.addActionListener(this);
        searchSensBut.setHorizontalAlignment(SwingConstants.CENTER);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(5,250,5,250);
        topSensPanel.add(searchSensBut, c);

        resultsFoundLbl = new JLabel("No Results Found");
        resultsFoundLbl.setFont(new Font("Helvetica", Font.BOLD, 18));
        resultsFoundLbl.setHorizontalAlignment(SwingConstants.CENTER);
        midSensPanel.add("North", resultsFoundLbl);

        // Sensors panel - table components
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.getSelectionModel().addListSelectionListener(this);
        table.setRowHeight(30);

        // Set the column widths in the table
        columnModel = table.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++)
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
   
        scrollPane = new JScrollPane(table);
        midSensPanel.add("Center", scrollPane);

        // Sensors panel - bottom
        botSortPanel = new JPanel(new GridLayout(4,1,10,10));
        botSortPanel.setBorder(new EmptyBorder(6,20,20,20));
        botVisPanel = new JPanel(new GridLayout(5,1,20,20));
        botSensPanel.add("West", botSortPanel);
        botSensPanel.add("East", botVisPanel);
        botSensPanel.setBorder(new EmptyBorder(20,20,20,20));

        // Sensors panel - sort section
        sortLbl = new JLabel("Sort Sensors By:");
        sortLbl.setHorizontalAlignment(SwingConstants.CENTER);
        sortLbl.setFont(new Font("Helvetica", Font.BOLD, 18));
        sortLbl.setForeground(new Color(9, 137, 11));

        applySortBtn = new JButton("Apply");
        applySortBtn.addActionListener(this);

        botSortPanel.add(sortLbl);
        botSortPanel.add(sortOpts);
        botSortPanel.add(applySortBtn);

        // Sensors panel - visualise panel
        visualiseAsLbl = new JLabel("Visualise Sensor Data As: ");
        visualiseAsLbl.setHorizontalAlignment(SwingConstants.CENTER);
        visualiseAsLbl.setFont(new Font("Helvetica", Font.BOLD, 18));
        visualiseAsLbl.setForeground(new Color(9, 137, 11));

        applyVisBtn = new JButton("Apply");
        applyVisBtn.addActionListener(this);

        plotOptLbl = new JLabel("Choose Graph Detail:");
        plotOptLbl.setHorizontalAlignment(SwingConstants.CENTER);
        plotOptLbl.setFont(new Font("Helvetica", Font.BOLD, 16));
        plotOptLbl.setForeground(new Color(9, 137, 11));

        botVisPanel.add(visualiseAsLbl);
        botVisPanel.add(visOpts);
        botVisPanel.add(plotOptLbl);
        botVisPanel.add(plotOpts);
        botVisPanel.add(applyVisBtn);

        // STATISTICS panels
        statsPanel = new JPanel(new BorderLayout());
        topStatPanel = new JPanel(new GridBagLayout());
        midStatPanel = new JPanel(new GridLayout(5,1,40,40));
        midStatPanel.setBorder(new EmptyBorder(10,10,10,10));
        botStatPanel = new JPanel(new GridBagLayout());

        statsPanel.add("North", topStatPanel);
        statsPanel.add("Center", midStatPanel);
        statsPanel.add("South", botStatPanel);

        statsLbl = new JLabel("Data Statistics:");
        statsLbl.setHorizontalAlignment(SwingConstants.CENTER);
        statsLbl.setFont(new Font("Helvetica", Font.BOLD, 26));
        statsLbl.setForeground(Color.RED);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10,0,10,0);
        topStatPanel.add(statsLbl, c);

        findDeviceLbl = new JLabel("Select a Device:");
        findDeviceLbl.setHorizontalAlignment(SwingConstants.CENTER);
        findDeviceLbl.setFont(new Font("Helvetica", Font.ITALIC, 22));
        findDeviceLbl.setForeground(Color.GRAY);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10,0,10,0);
        topStatPanel.add(findDeviceLbl, c);

        deviceStatOpts.addItem("<No File Opened>");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(10,250,10,250);
        topStatPanel.add(deviceStatOpts, c);

        findStatsForDeviceBtn = new JButton("Show Statistics");
        findStatsForDeviceBtn.addActionListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 20;
        c.gridx = 0;
        c.gridy = 3;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.insets = new Insets(5,250,40,250);
        topStatPanel.add(findStatsForDeviceBtn, c);

        midStatPanel.add(new BarGraphComponent(sensorMinStatValues, "Minimum Sensor Values", 2));
        midStatPanel.add(new BarGraphComponent(sensorMaxStatValues, "Maximum Sensor Values", 2));
        midStatPanel.add(new BarGraphComponent(sensorAvgStatValues, "Average Sensor Values", 2));
        midStatPanel.add(new BarGraphComponent(errorStatValues, "General Error Statistics", 1));

        statisticsFileLbl = new JLabel("No File Opened");
        statisticsFileLbl.setHorizontalAlignment(SwingConstants.CENTER);
        statisticsFileLbl.setFont(new Font("Helvetica", Font.BOLD, 20));
        statisticsFileLbl.setForeground(Color.BLACK);
        midStatPanel.add(statisticsFileLbl);

        exportBtn = new JButton("Save Graphs To File");
        exportBtn.addActionListener(this);
        c.gridwidth = 3;
        c.insets = new Insets(10,0,10,0);
        botStatPanel.add(exportBtn, c);

        // EVENTS panels
        eventsPanel = new JPanel(new BorderLayout());
        topEventsPanel = new JPanel(new GridBagLayout());
        midEventsPanel = new JPanel(new BorderLayout());
        botEventsPanel = new JPanel(new BorderLayout());

        eventsPanel.add("North", topEventsPanel);
        eventsPanel.add("Center", midEventsPanel);
        eventsPanel.add("South", botEventsPanel);

        eventsTitleLbl = new JLabel("Events");
        eventsTitleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        eventsTitleLbl.setFont(new Font("Helvetica", Font.BOLD, 28));
        eventsTitleLbl.setForeground(new Color(142, 199, 152));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(20,0,0,0);
        topEventsPanel.add(eventsTitleLbl, c);

        viewSensorsFoundLbl = new JLabel("Select A Unique Sensor Device To View Events");
        viewSensorsFoundLbl.setHorizontalAlignment(SwingConstants.CENTER);
        viewSensorsFoundLbl.setFont(new Font("Helvetica", Font.BOLD, 22));
        viewSensorsFoundLbl.setForeground(Color.GRAY);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 30;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10,0,0,0);
        topEventsPanel.add(viewSensorsFoundLbl, c);

        deviationExplLbl = new JLabel("An event is defined as a record with a deviation value greater than or equal to 20% about the mean");
        deviationExplLbl.setHorizontalAlignment(SwingConstants.CENTER);
        deviationExplLbl.setFont(new Font("Helvetica", Font.ITALIC, 18));
        deviationExplLbl.setForeground(Color.GRAY);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 2;
        topEventsPanel.add(deviationExplLbl, c);

        sensorNameLbl = new JLabel("Device Address:");
        sensorNameLbl.setHorizontalAlignment(SwingConstants.CENTER);
        sensorNameLbl.setFont(new Font("Helvetica", Font.BOLD, 18));
        sensorNameLbl.setForeground(new Color(142, 199, 152));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(30,30,10,500);
        topEventsPanel.add(sensorNameLbl, c);

        sensorOptsEvents.addItem("<No File Opened>");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 4;
        c.insets = new Insets(10,30,30,500);
        topEventsPanel.add(sensorOptsEvents, c);

        sensorNoLbl = new JLabel("Sensor Number:");
        sensorNoLbl.setHorizontalAlignment(SwingConstants.CENTER);
        sensorNoLbl.setFont(new Font("Helvetica", Font.BOLD, 18));
        sensorNoLbl.setForeground(new Color(142, 199, 152));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(30,500,10,30);
        topEventsPanel.add(sensorNoLbl, c);

        for (int i = 1; i <= 10; i++)
            sensorNoOpts.addItem(Integer.toString(i));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 4;
        c.insets = new Insets(10,500,30,30);
        topEventsPanel.add(sensorNoOpts, c);

        findEventsForSensBtn = new JButton("Find Events");
        findEventsForSensBtn.addActionListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 20;
        c.gridx = 0;
        c.gridy = 5;
        c.gridheight = 1;
        c.insets = new Insets(5,300,20,300);
        topEventsPanel.add(findEventsForSensBtn, c);

        // Events panel - table components
        tableModel = new DefaultTableModel(eventNames, 0);
        eventsTable = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventsTable.setRowHeight(30);
        noCsv = new Object[] {"<Search for a device>"};
        tableModel.addRow(noCsv);
        scrollPane = new JScrollPane(eventsTable);
        midEventsPanel.add("Center", scrollPane);

        saveEventsToFileBtn = new JButton("Save Events to File");
        saveEventsToFileBtn.addActionListener(this);
        saveEventsToFileBtn.setHorizontalAlignment(SwingConstants.CENTER);
        botEventsPanel.add(saveEventsToFileBtn);

        // Tab pane
        tabPane = new JTabbedPane();
        tabPane.add("Home", homePanel);
        tabPane.setBackgroundAt(0, Color.BLUE);
        tabPane.add("Sensors", sensorPanel);
        tabPane.setBackgroundAt(1, Color.GREEN);
        tabPane.add("Data Statistics", statsPanel);
        tabPane.setBackgroundAt(2, Color.RED);
        tabPane.add("Events", eventsPanel);
        tabPane.setBackgroundAt(3, new Color(142, 199, 152));
        tabPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        add(tabPane, BorderLayout.CENTER);
    }

    /**
     * A method to display the UI to the user thorugh the event-dispatching thread.
     */
    public void displayScreen() 
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
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
        if (table.getSelectedRow() >= 0 && e.getValueIsAdjusting())
            displayInfoScreen();
    }

    /**
     * Detects if a button on the UI has been pressed.
     * Allows the user to search for a sensor device, sort data in the table, visualise data as graphs and search for a CSV file.
     * @param e The action event instance.
     */
    public void actionPerformed(ActionEvent e) 
    {
        // Open a CSV file
        if (e.getSource() == openFileBtn) {
            if (data.findFile()) {
                fileOpenedLbl.setText(data.getFileName());
                statisticsFileLbl.setText(data.getFileName());

                // Create bar graph with statistics on data from chosen file
                errorsArray = data.findNoOfErrors();
                errorStatValues[0] = data.getNoOfRecords();
                errorStatValues[1] = errorsArray[0];
                errorStatValues[2] = errorsArray[1];

                // Update relevant labels
                linesLbl.setText(Integer.toString(data.getNoOfRecords()));
                errorsLbl.setText(Integer.toString(data.findNoOfErrors()[0]));

                errorValue = (double) (((errorsArray[0] / (double) data.getNoOfRecords()) * 100));
                percentLbl.setText(String.format("%.2f", errorValue) + "%");

                devicesFoundNoLbl.setText(Integer.toString(data.findNoOfUniqueDevices()));
                firstDateLbl.setText(data.findFirstDate());
                recentReadingLbl.setText(data.findRecentDate());

                // Reset table with containing data
                tableModel = (DefaultTableModel) table.getModel();
                tableModel.getDataVector().removeAllElements();
                tableModel.fireTableDataChanged();
                resultsFoundLbl.setText("No Results Found");

                // Populate combobox with unique sensors on events panel
                populateSensorsCombo();
            }
        }

        // Allows the user to quit the program
        else if (e.getSource() == quitBtn) {
            System.exit(0);
        }

        // Search for device by address and populate the table
        else if (e.getSource() == searchSensBut) {
            // Re-populate table with data from user input device address and populate date comboboxes
            populateTableData();
        }

        // Sort the data in the table and populate date comboboxes
        else if (e.getSource() == applySortBtn) {
            if (devicesFound.size() == 0)
                JOptionPane.showMessageDialog(new JFrame(), "Error - no data to sort! Please search for a device first.", "Error", JOptionPane.ERROR_MESSAGE);   
            else
                populateTableData();
        }

        // Visualise data as graphs
        else if (e.getSource() == applyVisBtn) {         
            // Retrieve user input for graph option and date ranges
            selectedItem = visOpts.getSelectedItem().toString();

            // Ensure data is in ordered ascending form
            populateTableData();
            data.sortData(sorts[1], devicesFound, sorts);

            // Display all the graphs
            if (devicesFound.size() == 0)
                JOptionPane.showMessageDialog(new JFrame(), "Error - no data to visualise! Please search for a device first.", "Error", JOptionPane.ERROR_MESSAGE);   
            else {
                // Schedule a job for the event-dispatching thread: creating + showing the graph UI.
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        displayGraphs();
                    }
                }); 
            } 
        }

        // Check for button click on statistics screen when selecting a device to view statistics for
        else if (e.getSource() == findStatsForDeviceBtn) {
            displayStatisticsForSensor(deviceStatOpts.getSelectedItem().toString());
            revalidate();
            repaint();
        }

        // Check for button export button click on statistics screen
        else if (e.getSource() == exportBtn) {
            saveGraphToFile(midStatPanel);
        }

        // Check for button click on events panel to display all events for a particular sensor
        else if (e.getSource() == findEventsForSensBtn) {
            if (data.getAllData().size() == 0)
                JOptionPane.showMessageDialog(new JFrame(), "Error - no data to find events for! Please open a CSV file first.", "Error", JOptionPane.ERROR_MESSAGE);   
            else 
                displaySensorEvents();
        }

        // Check for button click on saving all events found to a text file (output)
        else if (e.getSource() == saveEventsToFileBtn) {
            saveEventsToFile(eventsFound, sensorOpts.getSelectedItem().toString());
        }

        // Check for close button on the info pop up window
        else if (e.getSource() == quitInfoBtn) {
            infoWindow.dispose();
        }

        // Check for event/export button click on graph panels
        else {
            for (int i = 0; i < exportBtns.length; i++)
                if (e.getSource() == exportBtns[i])
                    saveGraphToFile(graphPanels[i]);
        }    
    }

    /**
     * A method to display a UI with a tabbed pane with all sensors found for a particular device.
     */
    private void displayGraphs() 
    {
        // Window details
        graphWindow = new JFrame("Scatter Graphs");
        graphTabPane = new JTabbedPane();
        graphTabPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        graphWindow.setLayout(new BorderLayout());
        graphWindow.add(graphTabPane, BorderLayout.CENTER);

        // Create flagged list
        flaggedDataPoints = new LinkedList<Integer>();

        // Check if devices found has fewer than 6 dates, if so use all dates to fill up space
        if (devicesFound.size() < 6)
            JOptionPane.showMessageDialog(new JFrame(), "Insufficient data to plot. 6 or more devices are required to plot the graphs.", "Error", JOptionPane.ERROR_MESSAGE);   
        else {   
            if (devicesFound.size() > 50 && visOpts.getSelectedItem().equals(visuals[2]) && plotOpts.getSelectedItem().equals(plots[0]) 
                || ((visOpts.getSelectedItem().equals(visuals[4]) || visOpts.getSelectedItem().equals(visuals[5]))) && plotOpts.getSelectedItem().equals(plots[0]))
                
                JOptionPane.showMessageDialog(new JFrame(), "Error - graph could not be plotted: too much sensor data.", "Error", JOptionPane.ERROR_MESSAGE);   
            
            else if (devicesFound.size() < 50 && ((plotOpts.getSelectedItem().equals(plots[1])) || (plotOpts.getSelectedItem().equals(plots[2]))))
                JOptionPane.showMessageDialog(new JFrame(), "Error - graph could not be plotted: graph detail is too small for the number of devices found.", "Error", JOptionPane.ERROR_MESSAGE);   

            else {
                sensInc = 1;

                // Used to obtain index in frequency array - then will be used to access int values for plot
                frequencyPlotValues[0][0] = devicesFound.size();
                frequencyPlotValues[0][1] = devicesFound.size()/5;
                index = -1;
                for (int i=0; i<plots.length; i++) {
                    if (plots[i].equals(plotOpts.getSelectedItem())) {
                        index = i;
                        break;
                    }
                }

                // Plot all data graphs for all sensors
                for (int i = 1; i <= 10; i++) {
                    // Retrieve sensor name and create new panel
                    sensorString = "Sensor " + i;
                    graphPanels[i-1] = new JPanel(new BorderLayout());

                    // Iterate over devices found and extract individual sensor values into linked list CHANGE!!!
                    listIt = devicesFound.listIterator();
                    sensorPoints = new LinkedList<Integer>();
                    datePoints = new LinkedList<String>();

                    // Work out increments between sensor points
                    deviceCounter = 0;
                    increment = (devicesFound.size()/frequencyPlotValues[index][0]);
                    runningIncrement = 0;
                    dateInc = frequencyPlotValues[index][1] - 1;

                    // Add occasional date between data points - used to display on graph component
                    while (listIt.hasNext()) {
                        deviceToCheck = listIt.next();

                        // Obtain points every now and again (increment)
                        if (deviceCounter == runningIncrement && sensorPoints.size() < frequencyPlotValues[index][0]) { 
                            runningIncrement += increment;

                            // Add the sensor point
                            try {
                                addSensorPoint(deviceToCheck, sensInc);
                            }
                            catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                                JOptionPane.showMessageDialog(new JFrame(), "Error - some data could not be converted successfully for this device.", "Error", JOptionPane.ERROR_MESSAGE);   
                            }
                            finally {
                                dateInc++;

                                // For every X data plots, write date string on X axis
                                if (dateInc == frequencyPlotValues[index][1]) {
                                    addDatePoint(deviceToCheck);
                                    dateInc = 0;
                                }
                            }
                        }
                        deviceCounter++;
                    }

                    // Add final sensor and date point values after while loop has finished
                    try {
                        addSensorPoint(deviceToCheck, sensInc);
                        addDatePoint(deviceToCheck);
                    }
                    catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        JOptionPane.showMessageDialog(new JFrame(), "Error - some data could not be converted successfully for this device.", "Error", JOptionPane.ERROR_MESSAGE);   
                    }
                    finally {
                        // Display error message detailing data that couldn't be plotted
                        if (sensorPoints.size() == 0) {
                            errorLbl = new JLabel("Data could not be plotted for this sensor. See table below for details:");
                            errorLbl.setHorizontalAlignment(SwingConstants.CENTER);
                            errorLbl.setFont(new Font("Helvetica", Font.BOLD, 15));
                            errorLbl.setForeground(Color.RED);
                            graphPanels[i-1].add("Center", errorLbl);
                            graphTabPane.add(sensorString, graphPanels[i-1]);
                        }
                        else {
                            // Prepare title string for graph plotting
                            title_details = ("Sensor " + i + " - Device Address " + deviceToCheck.getAddress());

                            // Get Max,Min,Avg data from the specific plot into a single string
                            graphDetails = data.getGraphDetails(sensInc-1, sensInc+1, deviceToCheck.getAddress());

                            // Ensure sensor points are sorted in ascending order
                            Collections.sort(devicesFound, (DataLine data_1, DataLine data_2) -> data_1.getTime() - data_2.getTime());

                            // Add new graph type component to new panel
                            if (visOpts.getSelectedItem().equals("Sensor-Value-Over-Time Line Graph")) 
                                graphPanels[i-1].add("Center", new GraphComponent(sensorPoints, datePoints, flaggedDataPoints, title_details, graphDetails, false, false, false));
                            
                            else if (visOpts.getSelectedItem().equals("Sensor-Value-Over-Time Line Graph (DASHED)")) 
                                graphPanels[i-1].add("Center", new GraphComponent(sensorPoints, datePoints, flaggedDataPoints, title_details, graphDetails, true, false, false));

                            else if (visOpts.getSelectedItem().equals("Scatter Graph")) 
                                graphPanels[i-1].add("Center", new GraphComponent(sensorPoints, datePoints, flaggedDataPoints, title_details, graphDetails, false, true, false));

                            else if (visOpts.getSelectedItem().equals("Scatter Graph (JOINED)")) 
                                graphPanels[i-1].add("Center", new GraphComponent(sensorPoints, datePoints, flaggedDataPoints, title_details, graphDetails, true, true, false));

                            else if (visOpts.getSelectedItem().equals("Bar Graph")) 
                                graphPanels[i-1].add("Center", new GraphComponent(sensorPoints, datePoints, flaggedDataPoints, title_details, graphDetails, false, false, true));
                            
                            else if (visOpts.getSelectedItem().equals("Bar Graph (DASHED)")) 
                                graphPanels[i-1].add("Center", new GraphComponent(sensorPoints, datePoints, flaggedDataPoints, title_details, graphDetails, true, false, true));
                            
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
     * Simple method to populate the sensors options combobox on the events panel.
     */
    private void populateSensorsCombo()
    {
        sensorOpts.removeAllItems();
        sensorOptsEvents.removeAllItems();
        deviceStatOpts.removeAllItems();

        // Populate the sensors combobox
        sensorEventsList = new LinkedList<DataLine>();
        sensorEventsList = data.findUniqueDevices();

        listIt = sensorEventsList.listIterator();

        // Add each address to the available options in each combobox on the UI
        while (listIt.hasNext()) {
            deviceToAdd = listIt.next();
            sensorOpts.addItem(deviceToAdd.getAddress());
            sensorOptsEvents.addItem(deviceToAdd.getAddress());
            deviceStatOpts.addItem(deviceToAdd.getAddress());
        }
    }

    /**
     * Method to display all the events found for a chosen sensor in a simple table on the events panel.
     */
    private void displaySensorEvents()
    {
        // Clear table contents
        tableModel = (DefaultTableModel) eventsTable.getModel();
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();

        // Clear events found linked list
        while (!eventsFound.isEmpty())
            eventsFound.removeFirst();

        // Initialise relevant variables to to store devices found and means found for those devices
        deviceEventsList = data.findDeviceByAddress(sensorOptsEvents.getSelectedItem().toString());
        sensorMeans = new ArrayList<Integer>();
        errorInConversion = false;
        sensInc = 1;

        // Add each mean to the linked list for displaying
        for (int i = 1; i <= 10; i++) {
            sensorMeans.add(data.getAvgVal(sensInc-1, sensInc+1, deviceEventsList));
            sensInc += 2;
        }

        // Value will change on each iteration of the while loop
        deviationString = "No Events Found";

        // Iterate over linked list and add to output
        listIt = deviceEventsList.listIterator();
        while (listIt.hasNext()) {

            // Obtain next device properties
            deviceToAdd = listIt.next();
            deviationValues = new ArrayList<Integer>();

            // Initialise variables for scanning sensor means
            deviationValue = 0;
            sensInc = 1;
            errorInConversion = false;
            sensorIndex = Integer.parseInt(sensorNoOpts.getSelectedItem().toString()) * 2;

            // Obtain deviation from mean for each sensor mean values
            try {
                deviationValue = data.getDeviationFromMean(sensorMeans.get((sensorIndex/2) - 1), Integer.parseInt(deviceToAdd.getSensorData().substring(sensorIndex-2, sensorIndex), 16));
            }
            catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                errorInConversion = true;
            }

            // If successfull and an event has been detected, update the string and add to table
            if (!errorInConversion)
                if (deviationValue >= 20) {
                    deviationString = "Sensor " + sensorIndex/2 + " on device " + deviceToAdd.getAddress() + " showed a deviation value of " + deviationValue + "% on " + deviceToAdd.getDateObtained();
                    
                    // Store properties from data line
                    dataToAdd = new Object[] {deviationString};
                    eventsFound.add(deviationString);
                    
                    // Add row to table
                    tableModel.addRow(dataToAdd);
                }
            sensInc += 2;
        }

        // Error checking/range checking
        if (deviationString.equals("No Events Found")) {
            Object[] dataToAdd = {deviationString};
            tableModel.addRow(dataToAdd);
        }
        if (errorInConversion)
            JOptionPane.showMessageDialog(new JFrame(), "Error - some data could not be converted successfully for this device.", "Error", JOptionPane.ERROR_MESSAGE);   
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
        // Error checking
    	if (data.getAllData().size() == 0)
    		return;
    	else
       		devicesFound = data.findDeviceByAddress(sensorOpts.getSelectedItem().toString());

        if (devicesFound.size() != 0) {
            // Sort the data in the table from user input
            data.sortData(sortOpts.getSelectedItem().toString(), devicesFound, sorts);

            // Iterate over linked list and add to output
            listIt = devicesFound.listIterator();

            while (listIt.hasNext()) {
                // Obtain next device properties
                deviceToAdd = listIt.next();

                // Store properties from data line
                Object[] dataToAdd = {
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
            resultsFoundLbl.setText("Results Found: " + devicesFound.size());
        }
        else
            resultsFoundLbl.setText("No Results Found");
    }

    /**
     * Method to add a sensor point and detect if an error has been found - if so, add to linked list of flagged data points.
     * @param deviceToCheck The current device under consideration.
     * @param sensInc The index for which sensor values can be obtained for the given device to check.
     */
    private void addSensorPoint(DataLine deviceToCheck, int sensInc) 
    {
        sensorValue = Integer.parseInt(deviceToCheck.getSensorData().substring(sensInc-1,sensInc+1), 16);
        sensorPoints.add(sensorValue);

        // Check for errors
        if (!(deviceToCheck.getStatus().equals("0") || deviceToCheck.getStatus().equals("00"))) {
            flaggedDataPoints.add(0); // 0 =flagged ("fail")
        }
        else {
            flaggedDataPoints.add(1); // 1 =success
        }
    }

    /**
     * Method to add a date value to the linked list of date points.
     * @param deviceToCheck The current device under consideration.
     */
    private void addDatePoint(DataLine deviceToCheck) 
    {
        dateValue = deviceToCheck.getDateObtained();
        datePoints.add(dateValue);
    }

    /**
     * Method to find the statistics for a given device (used on the statistics screen).
     * The user can view maximum, minimum and average values in the form of bar graphs.
     * @param device The curretn device under consideration.
     */
    private void displayStatisticsForSensor(String device)
    {
        // Error checking
    	if (data.getAllData().size() == 0)
    		return;
    	else
	        for (int i = 1; i <= 10; i++)
	            if (data.getMaxVal(i-1, i+1, data.findDeviceByAddress(device)) == -1 || data.getMinVal(i-1, i+1, data.findDeviceByAddress(device)) == -1) {
	                JOptionPane.showMessageDialog(new JFrame(), "Error - some maximum and minimum values couldn't be calculated as some data has been found in the wrong format.", "Error", JOptionPane.ERROR_MESSAGE);   
	                break;
	            }

        // Find statistics for the given device
        for (int i = 1; i < 20; i+=2) {
            sensorMinStatValues[i/2] = data.getMinVal(i-1, i+1, data.findDeviceByAddress(device));
            sensorMaxStatValues[i/2] = data.getMaxVal(i-1, i+1, data.findDeviceByAddress(device));
            sensorAvgStatValues[i/2] = data.getAvgVal(i-1, i+1, data.findDeviceByAddress(device));
        }
    }

    /**
     * Method to display a simple pop up window displaying all the details about the user selected row.
     * The user selects a row from the device table and the window appears.
     */
    private void displayInfoScreen() 
    {       
        infoWindow = new JFrame();
        mainInfoPanel = new JPanel(new BorderLayout());
        topInfoPanel = new JPanel();
        midInfoPanel = new JPanel(new GridLayout(10,2));
        midInfoPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        botInfoPanel = new JPanel(new BorderLayout());
        botInfoPanel.setBorder(new EmptyBorder(5, 20, 20, 20));

        infoTitle = new JLabel("Data Information");
        infoTitle.setHorizontalAlignment(SwingConstants.CENTER);
        infoTitle.setFont(new Font("Helvetica", Font.BOLD, 24));
        infoTitle.setForeground(new Color(9, 137, 11));
        topInfoPanel.add(infoTitle);

        deviceAddressLbl = new JLabel("Device Address: ");
        deviceTypeLbl = new JLabel("Device Type: ");
        deviceVersionLbl = new JLabel("Device version: ");
        errorsMissedLbl = new JLabel("Errors missed by Receiver (current total): ");
        viaLbl = new JLabel("Receiver that picked up this Transmission: ");
        statusLbl = new JLabel("Device Status: ");
        dateLbl = new JLabel("Date Obtained: ");
        dataLbl = new JLabel("Sensor Data:");

        addressDetails = new JLabel(table.getValueAt(table.getSelectedRow(), 5).toString());
        typeDetails = new JLabel(table.getValueAt(table.getSelectedRow(), 1).toString());
        versionDetails = new JLabel(table.getValueAt(table.getSelectedRow(), 2).toString());
        errorsDetails = new JLabel(table.getValueAt(table.getSelectedRow(), 3).toString());
        viaDetails = new JLabel(table.getValueAt(table.getSelectedRow(), 4).toString());
        statusDetails = new JLabel(table.getValueAt(table.getSelectedRow(), 6).toString());
        dateDetails = new JLabel(table.getValueAt(table.getSelectedRow(), 0).toString());
        dataDetails = new JLabel(table.getValueAt(table.getSelectedRow(), 7).toString());

        generalNotesLbl = new JLabel("General Notes: ");
        notesDetailsLbl = new JLabel("No problems detected");

        midInfoPanel.add(deviceAddressLbl);
        midInfoPanel.add(addressDetails);
        midInfoPanel.add(deviceTypeLbl);
        midInfoPanel.add(typeDetails);
        midInfoPanel.add(deviceVersionLbl);
        midInfoPanel.add(versionDetails);
        midInfoPanel.add(errorsMissedLbl);
        midInfoPanel.add(errorsDetails);
        midInfoPanel.add(viaLbl);
        midInfoPanel.add(viaDetails);
        midInfoPanel.add(statusLbl);
        midInfoPanel.add(statusDetails);
        midInfoPanel.add(dateLbl);
        midInfoPanel.add(dateDetails);
        midInfoPanel.add(dataLbl);
        midInfoPanel.add(dataDetails);
        midInfoPanel.add(generalNotesLbl);
        midInfoPanel.add(notesDetailsLbl);

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

        populateInfoData(table.getValueAt(table.getSelectedRow(), 7).toString(), table.getValueAt(table.getSelectedRow(), 6).toString());

        scrollPane = new JScrollPane(infoTable);
        botInfoPanel.add("Center", scrollPane);

        quitInfoBtn = new JButton("Close");
        quitInfoBtn.addActionListener(this);
        botInfoPanel.add("South", quitInfoBtn);

        mainInfoPanel.add("North", topInfoPanel);
        mainInfoPanel.add("Center", midInfoPanel);
        mainInfoPanel.add("South", botInfoPanel);

        infoWindow.add(mainInfoPanel);
        infoWindow.setTitle("Sensor Data Information");
        infoWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        infoWindow.setLocation(100,100);
        infoWindow.setSize(720, 700);
        infoWindow.setResizable(false);
        infoWindow.setVisible(true);
    }

    /**
     * Method to insert all relevant sensor data into the info table on the info pop up window.
     * @param dataString The sensor data string to break down and display to the user.
     * @param statusString The current status of the sensor under consideration.
     */
    private void populateInfoData(String dataString, String statusString) 
    {
        sensInc = 1;

        // Iterate over all the sensor data in the data string and find errors
        for (int i = 1; i <= dataString.length()/2; i++) {
            try {
                sensorDecValue = Integer.parseInt(dataString.substring(sensInc-1,sensInc+1), 16);
                if (!(statusString.equals("00") || statusString.equals("0")))
                    notesDetailsLbl.setText("Errors found - see status field.");
            }
            catch (NumberFormatException e) {
                notesDetailsLbl.setText("Errors found - couldn't recognise some sensor data.");
                sensorDecValue = 0;
            }

            // Add sensor number, decimal value equivalent and hexadecimal version into the table
            Object[] dataToAdd = {
                Integer.toString(i),
                sensorDecValue,
                sensorHexValue = dataString.substring(sensInc-1,sensInc+1),
            };

            infoTableModel.addRow(dataToAdd);
            sensInc += 2;
        } 
    }

    /**
     * Method to save painted components on a panel to a png file in the users chosen directory.
     * @param panel The panel that contains the components to be saved to a png file.
     */
    private void saveGraphToFile(JPanel panel) 
    {
        selectDest = new JFileChooser();
        selectDest.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        selectDest.showSaveDialog(null);

        img = new BufferedImage(panel.getWidth(), panel.getHeight()-40, BufferedImage.TYPE_INT_RGB);
        panel.paint(img.getGraphics());
        try {
            ImageIO.write(img, "png", new File(selectDest.getSelectedFile().getPath() + ".png"));
            JOptionPane.showMessageDialog(new JFrame(), "Graph(s) saved successfully!", "Success", JOptionPane.PLAIN_MESSAGE);   
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(new JFrame(), "No file saved.", "Info", JOptionPane.PLAIN_MESSAGE);   
        }
    }   

    /**
     * Method to write all the events found in the events table to a text file output.
     * @param eventsFound The linked list of all the events found for a particular device.
     * @param deviceAddress The current device under consideration.
     */
    private void saveEventsToFile(LinkedList<String> eventsFound, String deviceAddress) 
    {
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  
        chooser.showSaveDialog(null);
        filename = chooser.getSelectedFile().toString() + ".txt";
        eventsIt = eventsFound.listIterator();       

        // Create a new text file and the users chosen location
        try {
            file = new BufferedWriter(new FileWriter(filename));
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(), "No File Saved.", "Info", JOptionPane.PLAIN_MESSAGE);   
            return;
        }

        // Write each event to a text file on separate lines
        while (eventsIt.hasNext()) {
            nextEvent = eventsIt.next();
            try {
                file.write(nextEvent);
                file.newLine();
            }
            catch (IOException e) {
                return;
            } 
        }

        // Close the file and check for success
        try {
            file.close();
            JOptionPane.showMessageDialog(new JFrame(), "Text file saved successfully!", "Success", JOptionPane.PLAIN_MESSAGE);   
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(), "No text file saved.", "Info", JOptionPane.PLAIN_MESSAGE);   
            return;
        }
    }   
}