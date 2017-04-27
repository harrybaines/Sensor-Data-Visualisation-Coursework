import java.io.*;
import java.util.Scanner;
import java.awt.*;
import java.util.*;
import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class provides functionality for simple operations on CSV files.
 *
 * @author Harry Baines
 */
public class SensorData
{
	// Calendar/Date instance variables
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.UK);
    private GregorianCalendar cal;

    // Data and file variables
    private JFileChooser source;
    private FileNameExtensionFilter filter;
    private File selectedFile;
    private BufferedReader reader;
    private String line = "";
	private String[] dataLine;

    // Linked list to store data lines from CSV file
    private LinkedList<DataValue> dataList = new LinkedList<DataValue>();

    /**
     * Allows the user to open a CSV file of their choice which contains sensor data.
     */
	public void findFile()
	{
		// Clear linked list
		while (!dataList.isEmpty()) {
	        dataList.removeFirst();
	    }

		// Swing component = file request dialog
		source = new JFileChooser();
		filter = new FileNameExtensionFilter("Comma Seperated Files", "csv"); 
		source.setAcceptAllFileFilterUsed(false);
		source.setFileFilter(filter);

		

		if (source.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
		{   
			selectedFile = source.getSelectedFile();

			String fileString = selectedFile.toString();

			String extension = fileString.substring(fileString.lastIndexOf(".") + 1, fileString.length());

			if (!extension.equals("csv")) {
			    JOptionPane.showMessageDialog(new JFrame(), "Error - please choose a CSV file!", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				try 
				{
					reader = new BufferedReader(new FileReader(selectedFile));
		            while ((line = reader.readLine()) != null) 
		            {
		                // Comma = separator
		                dataLine = line.split(",");

		                // Add data components to single data entity, then add to array list
		                dataList.add(new DataValue(dataLine[0], dataLine[1], dataLine[2], dataLine[3], dataLine[4],
		                	dataLine[5], dataLine[6], dataLine[7], addSecondsToDate(Integer.parseInt(dataLine[0]))));

		                // Retrieve a data item
		                // DataValue cur_data = dataList.get(0);
		                // System.out.println(cur_data.getTime());

		                System.out.println("Time: " + dataLine[0] + ", Type: " + dataLine[1] + ", Version: " + dataLine[2]
		                	+ ", Counter: " + dataLine[3] + ", Via: " + dataLine[4] + ", Address: " + dataLine[5]
		                	+ ", Status: " + dataLine[6] + ", Sensor Data: " + dataLine[7]);

		                System.out.println("Date: " + addSecondsToDate(Integer.parseInt(dataLine[0])) + "\n");
		            }

		            // Show success dialog
					JOptionPane.showMessageDialog(new JFrame(), "File successfully opened!");
				}
				catch (FileNotFoundException e) {
	            	e.printStackTrace();
				}
	       		catch (IOException e) {
	            	e.printStackTrace();
	       		}
			}
        }
		else 
			System.out.println("No file chosen!");
	}

	/**
	 * Allows the user to search for a device by address.
	 * @param address The address of the device the user wishes to search for.
	 */
	private void findDeviceByAddress(String address)
	{
		
	}

	/**
	 * Adds a specified amount of seconds to the date set at the year 2000.
	 *
	 * @param s The number of seconds to add to the date.
	 * @return The date as a string in a human-readable form.
	 */
	private String addSecondsToDate(int s)
	{
        cal = new GregorianCalendar(2000,00,01,0,0,0);
        cal.add(Calendar.SECOND, s);
        return (dateFormat.format(cal.getTime()));
	}

	/**
	 * Main method to set the initial time zone and create relevant instances to begin the program.
	 * @param args Unused.
	 */
	public static void main(String[] args)
	{
		// Set time zone
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		// Schedule a job for the event-dispatching thread: creating + showing the GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainScreen m = new MainScreen();
                m.displayScreen();
            }
        });
	}
}