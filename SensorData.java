import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.text.SimpleDateFormat;

/**
 * This class provides functionality for simple operations on CSV files.
 * A date can also be calculated given a number of seconds since the year 2000.
 *
 * @author Harry Baines
 */
public class SensorData
{
	// Calendar/Date instance variables
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss", Locale.UK);
    private GregorianCalendar cal;

    // Data and file variables
    private JFileChooser source;
    private FileNameExtensionFilter filter;
    private File selectedFile;
    private String fileString;
    private String extension;
    private BufferedReader reader;
    private String line = "";
	private String[] dataLine;
	private DataLine nextData;

    // Linked list to store data lines from CSV file
    private LinkedList<DataLine> dataList = new LinkedList<DataLine>();

    // Linked list to store all devices found when a search has occured
    private LinkedList<DataLine> devicesFound = new LinkedList<DataLine>();

    // List iterator to iterate over all data lines
    private ListIterator<DataLine> listIt;

	/**
	 * Simple constructor to set the time zone for date calculations.
	 */
	public SensorData()
	{
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

    /**
     * Allows the user to open a CSV file of their choice which contains sensor data.
     */
	public void findFile()
	{
		// Clear linked list
		while (!dataList.isEmpty())
	        dataList.removeFirst();

		// Swing component = file request dialog, only allow CSV's
		source = new JFileChooser();
		filter = new FileNameExtensionFilter("Comma Seperated Files", "csv"); 
		source.setAcceptAllFileFilterUsed(false);
		source.setFileFilter(filter);

		// Read CSV file
		if (source.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
		{   
			// Store file data into variables
			selectedFile = source.getSelectedFile();
			fileString = selectedFile.toString();
			extension = fileString.substring(fileString.lastIndexOf(".") + 1, fileString.length());

			// Error checking
			if (!extension.equals("csv")) 
			    JOptionPane.showMessageDialog(new JFrame(), "Error - please choose a CSV file!", "Error", JOptionPane.ERROR_MESSAGE);	
			else
			{
				try 
				{
					// Open the file and read line by line
					reader = new BufferedReader(new FileReader(selectedFile));
		            while ((line = reader.readLine()) != null) 
		            {
		                // Comma = separator
		                dataLine = line.split(",");

		                // Add data components to single data entity, then add to linked list
		                dataList.add(new DataLine(dataLine[0], dataLine[1], dataLine[2], dataLine[3], dataLine[4],
		                	dataLine[5], dataLine[6], dataLine[7], addSecondsToDate(Integer.parseInt(dataLine[0]))));
		            }
		            // Show success dialog
					JOptionPane.showMessageDialog(new JFrame(), "File successfully opened!");
				}
				catch (FileNotFoundException e) 
				{
	            	e.printStackTrace();
				}
	       		catch (IOException e) 
	       		{
	            	e.printStackTrace();
	       		}
			}
        }
		else 
			System.out.println("No file chosen!");
	}

	/**
	 * Allows the user to search for a device by address.
	 *
	 * @param address The address of the device the user wishes to search for.
	 * @return The linked list containing all device search results found.
	 */
	public LinkedList<DataLine> findDeviceByAddress(String address)
	{
		// Clear linked list
		while (!devicesFound.isEmpty())
	        devicesFound.removeFirst();

	    // Iterate over data lines - search for device, add to linked list
 		listIt = dataList.listIterator();

        while (listIt.hasNext())
        {
        	nextData = listIt.next();
        	if (nextData.getAddress().equals(address))
        		devicesFound.add(nextData);
        }
		return devicesFound;
	}

	/**
	 * Method to return the total number of errors found in the data provided.
	 * @return The total number of errors - index 0 = total error count, index 1 = different errors count 
	 */
	public int[] findNoOfErrors()
	{
		int[] errorsArray = new int[2];
		int differentErrorCount = 0;

	    // Iterate over all data lines and find errors, add to array
 		listIt = dataList.listIterator();

        while (listIt.hasNext())
        {
        	nextData = listIt.next();

        	System.out.println("Status: " + nextData.getStatus());

        	// Find an error
        	if (!(nextData.getStatus().equals("0") || (nextData.getStatus().equals("00"))))
        	{
        		errorsArray[0]++;
        	}

        	// Find total number of different errors
        	//errorsArray[1]++;
        	
        }
		return errorsArray;
	}

	/**
	 * Returns the name of the file opened.
	 * @return The opened file as a string.
	 */
	public String getFileName()
	{
		return ("Currently Using File: \"" + selectedFile.getName() + "\"");
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
}
