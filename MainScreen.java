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
    private JPanel botCompPanel;
    private JPanel botGraphPanel;
    private JPanel optionPanel;
    private JPanel topOptPanel;
    private JPanel midOptPanel;
    private JPanel botOptPanel;

    // UI Components
    // Home Panel
    private ScatterGraphComponent scatterGraph;
    private JButton button;

    // Sensors Panel
    private JLabel addressLbl;
    private JTextField addressEntry;
    private JButton searchSensBut;
    private JLabel resultsFoundLbl;

    // List of visualisation options for sensor devices
    private String[] visuals = new String[] {"Timeline", "Sensor-Value-Over-Time Line Graph", "Bar Chart", "Scatter Graph"};
    private JComboBox<String> visOpts = new JComboBox<String>(visuals);

    // Table variables
    private static final String[] columnNames = {"Time (s)", "Type", "Version", "Counter", "Via", "Address", "Status", "Sensor Data", "Date Obtained"};
    private LinkedList<Object> tableData = new LinkedList<Object>();
    private JTable table;
    private DefaultTableModel tableModel;
    private TableColumnModel columnModel;

    private JButton applyVisBtn;

    private ListIterator<DataLine> listIt;
    private DataLine deviceToAdd; 
    private JScrollPane scrollPane;

    // Options panel
    private JButton openFileBtn;



    // Graph Dialog Window Variables
    private LinkedList<DataLine> flaggedDataLines = new LinkedList<DataLine>();
    private DataLine deviceToCheck;
    private JTabbedPane graphTabPane;
    private int sensNo = 1;
    private int inc = 1;




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
        topSensPanel = new JPanel();
        midSensPanel = new JPanel(new BorderLayout());
        botSensPanel = new JPanel(new BorderLayout());

        // botSensPanel sub-panels
        botCompPanel = new JPanel();
        botGraphPanel = new JPanel();
        botSensPanel.add("North", botCompPanel);
        botSensPanel.add("South", botGraphPanel);

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

        resultsFoundLbl = new JLabel("No Results Found");
        topSensPanel.add(resultsFoundLbl);

        // Table components
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

        columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(60);
        columnModel.getColumn(1).setPreferredWidth(20);
        columnModel.getColumn(2).setPreferredWidth(20);
        columnModel.getColumn(3).setPreferredWidth(20);
        columnModel.getColumn(4).setPreferredWidth(10);
        columnModel.getColumn(5).setPreferredWidth(60);
        columnModel.getColumn(6).setPreferredWidth(20);
        columnModel.getColumn(7).setPreferredWidth(150);
        columnModel.getColumn(8).setPreferredWidth(170);

        scrollPane = new JScrollPane(table);
        midSensPanel.add("Center", scrollPane);

        JLabel visualiseAsLbl = new JLabel("Visualise Sensor Data As: ");
        visualiseAsLbl.setHorizontalAlignment(SwingConstants.LEFT);
        visualiseAsLbl.setFont(new Font("Helvetica", Font.BOLD, 15));
        visualiseAsLbl.setForeground(Color.RED);
        applyVisBtn = new JButton("Apply");
        applyVisBtn.addActionListener(this);

        botCompPanel.add(visualiseAsLbl);
        botCompPanel.add(visOpts);
        botCompPanel.add(applyVisBtn);

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
                window.setSize(900, 1000);
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

            if (devicesFound.size() == 0)
                resultsFoundLbl.setText("No Results Found");
            else
                resultsFoundLbl.setText("Results Found: " + devicesFound.size());
        }




        else if (e.getSource() == applyVisBtn)
        {
            if (visOpts.getSelectedItem().equals("Scatter Graph"))
            {               
                SwingUtilities.invokeLater(new Runnable() 
                {
                    @Override
                    public void run() 
                    {
                        JFrame f = new JFrame("Scatter Graphs");
                        graphTabPane = new JTabbedPane();
                        graphTabPane.setBorder(new EmptyBorder(10, 10, 10, 10));
                        setLayout(new BorderLayout());
                        add(graphTabPane, BorderLayout.CENTER);

                        int sensInc = 1;

                        for (int i = 1; i <= 10; i++)
                        {
                            String sensorString = "Sensor " + i;

                            JPanel graphPanel = new JPanel(new BorderLayout());

                            LinkedList<Integer> sensorPoints = new LinkedList<Integer>();

                            // IMPLEMENT LINKEDLIST OF DATE POINTS HERE, USE %i TO GET OCCASIONAL DATA INTO THIS STRUCTURE

                            // Iterate over devices found and extract individual sensor values
                            listIt = devicesFound.listIterator();

                            while (listIt.hasNext())
                            {
                                deviceToCheck = listIt.next();
                                int sensorValue = Integer.parseInt(deviceToCheck.getSensorData().substring(sensInc-1,sensInc+1), 16);
                                sensorPoints.add(sensorValue);
                            }

                            // Add new graph component to new panel
                            graphPanel.add("Center", new ScatterGraphComponent(sensorPoints));

                            // Add new panel to tab pane
                            graphTabPane.add(sensorString, graphPanel);
                            f.add(graphTabPane);

                            sensInc += 2;
                        }

                        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        f.setSize(1400,700);
                        f.setLocation(200,200);
                        f.setVisible(true);
                        f.setResizable(false); 
                    }
                }); 
            }
        }
    }
}





