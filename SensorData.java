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
	// Calendar/date instance variables
	private final SimpleDateFormat dateFormat;
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

	// Error variables
	private int[] errorsArray;
	private LinkedList<String> uniqueErrorsList = new LinkedList<String>(); // Linked list to store unique errors !!!!
	private int counter;

    private LinkedList<DataLine> dataList; // Linked list to store data lines from CSV file
    private LinkedList<DataLine> devicesFound; // Linked list to store all devices found when a search has occured
    private ListIterator<DataLine> listIt; // List iterator to iterate over all data lines

	/**
	 * Simple constructor to initialise the instance variables.
	 */
	public SensorData() 
	{
		dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss", Locale.UK);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		dataList = new LinkedList<DataLine>();
		devicesFound = new LinkedList<DataLine>();
		errorsArray = new int[2];
	}

    /**
     * Allows the user to open a CSV file of their choice which contains sensor data.
     */
	public boolean findFile() 
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
		if (source.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {   
			// Store file data into variables
			selectedFile = source.getSelectedFile();
			fileString = selectedFile.toString();
			extension = fileString.substring(fileString.lastIndexOf(".") + 1, fileString.length());

			// Error checking
			if (!extension.equals("csv")) {
			    JOptionPane.showMessageDialog(new JFrame(), "Error - please choose a CSV file!", "Error", JOptionPane.ERROR_MESSAGE);	
			    return false;
			}
			else {
				try {
					// Open the file and read line by line
					reader = new BufferedReader(new FileReader(selectedFile));
		            while ((line = reader.readLine()) != null) {
		                // Comma = separator
		                dataLine = line.split(",");

		                // Add data components to single data entity, then add to linked list
		                dataList.add(new DataLine(dataLine[0], dataLine[1], dataLine[2], dataLine[3], dataLine[4],
		                	dataLine[5], dataLine[6], dataLine[7], addSecondsToDate(Integer.parseInt(dataLine[0]))));
		            }
		            // Show success dialog
					JOptionPane.showMessageDialog(new JFrame(), "File successfully opened!");
				}
				catch (FileNotFoundException e) {
	            	return false;
				}
	       		catch (IOException e) {
	            	return false;
	       		}
			}
        }
		else {
            JOptionPane.showMessageDialog(new JFrame(), "No File Chosen", "Info", JOptionPane.PLAIN_MESSAGE);   
			return false;
		}	
		return true;
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

 		DataLine deviceToAdd = listIt.next();

        int currentTime = deviceToAdd.getTime();

        while (listIt.hasNext()) {

        	nextData = listIt.next();

        	int nextTime = nextData.getTime();

        	if (nextData.getAddress().equals(address) && nextTime != currentTime) {
        		devicesFound.add(nextData);
        		currentTime = nextTime;
        	}
        }

		Collections.sort(devicesFound, (DataLine data_1, DataLine data_2) -> data_2.getTime() - data_1.getTime());
		return devicesFound;
	}
	/**
	 * Method to find the total number of errors and readings found for a particular device by address.
	 * @return The total number of errors for this device - index 0 = number of sensor readings, index 1 = number of errors
	 */
	public int[] findErrorsByAddress(String address)
	{
		LinkedList<DataLine> dataLinesByAddress = findDeviceByAddress(address);

		int[] deviceDataArray = new int[2];

		// Iterate over all data lines and find errors, add to array
 		listIt = dataLinesByAddress.listIterator();

        while (listIt.hasNext()) {

        	nextData = listIt.next();

        	// Find an error
        	if (!(nextData.getStatus().equals("0") || nextData.getStatus().equals("00"))) 
        		deviceDataArray[0]++;

        	deviceDataArray[1]++;
        }

        return deviceDataArray;
	}

	/**
	 * Method to return the total number of errors found in the data provided.
	 * @return The total number of errors, where index 0 = total error count, index 1 = different errors found 
	 */
	public int[] findNoOfErrors() 
	{
		counter = 0;
	    errorsArray[0] = 0;
	    errorsArray[1] = 0;

	    // Clear linked list
		while (!uniqueErrorsList.isEmpty())
	        uniqueErrorsList.removeFirst();

	    // Iterate over all data lines and find errors, add to array
 		listIt = dataList.listIterator();

        while (listIt.hasNext()) {
        	nextData = listIt.next();

        	// Find an error
        	if (!(nextData.getStatus().equals("0") || nextData.getStatus().equals("00"))) {
        		errorsArray[0]++; 
        		uniqueErrorsList.add(nextData.getStatus());
        		counter++;
        	}
        }

        // Sort the array of errors (used to help find unique errors)
        if (uniqueErrorsList.size() > 0) {
        	Collections.sort(uniqueErrorsList, (String data_1, String data_2) -> data_2.compareTo(data_1));

	    	ListIterator<String> curStatusIt = uniqueErrorsList.listIterator();

	    	String currentStatus = curStatusIt.next();
	    	String nextStatus;

	    	while (curStatusIt.hasNext()) {
	    		nextStatus = curStatusIt.next();
	    		if (!(currentStatus.equals(nextStatus))) {
	    			// Used for first time round
	    			if (errorsArray[1] == 0)
	    				errorsArray[1] += 2;
	    			else
	    				errorsArray[1]++;

	    			currentStatus = nextStatus;
	    		}
	    	}

	    	// Increase by 1 to ensure if only 1 error is present, then 1 unique error will be present
	    	errorsArray[1]++;
        }
        
		return errorsArray;
	}

	/**
	 * Method to return the first date a sensor device picked up.
	 * @return A string of the first date.
	 */
	public String findFirstDate()
	{
		Collections.sort(dataList, (DataLine data_1, DataLine data_2) -> data_1.getTime() - data_2.getTime());
		return dataList.get(0).getDateObtained();
	}

	/**
	 * Method to find the total number of unique devices from the CSV file.
	 * @return The total number of unique devices.
	 */
	public int findNoOfUniqueDevices()
	{
		int noOfDevices = 0;
		boolean firstTime = true;
		String currentDeviceAddress;
		String nextAddress;
        Collections.sort(dataList, (DataLine data_1, DataLine data_2) -> data_2.getAddress().compareTo(data_1.getAddress()));

        listIt = dataList.listIterator();

       	currentDeviceAddress = listIt.next().getAddress();

        while (listIt.hasNext()) {

        	nextAddress = listIt.next().getAddress();

        	if (!(nextAddress.equals(currentDeviceAddress)) || firstTime) {
        		noOfDevices++;
        		currentDeviceAddress = nextAddress;
        		firstTime = false;
        	}
        }

		return noOfDevices;
	}

	/**
	 * Method to return the most recent date a sensor device reading was picked up.
	 * @return A string of the most recent date.
	 */
	public String findRecentDate()
	{
		Collections.sort(dataList, (DataLine data_1, DataLine data_2) -> data_2.getTime() - data_1.getTime());
		return dataList.get(0).getDateObtained();
	}

	/**
     * Method to sort the sensor data by various user input parameters.
     * @param user_selection The users selected sort option from the combobox.
     * @param list The linked list to sort.
     */
    public void sortData(String user_selection, LinkedList<DataLine> list, String[] sorts) 
    {
		if (user_selection.equals(sorts[0]))
			Collections.sort(list, (DataLine data_1, DataLine data_2) -> data_2.getTime() - data_1.getTime());

    	else if (user_selection.equals(sorts[1]))
    		Collections.sort(list, (DataLine data_1, DataLine data_2) -> data_1.getTime() - data_2.getTime());

    	else if (user_selection.equals(sorts[2]))
			Collections.sort(list, (DataLine data_1, DataLine data_2) -> data_2.getStatus().compareTo(data_1.getStatus()));

    	else if (user_selection.equals(sorts[3]))
			Collections.sort(list, (DataLine data_1, DataLine data_2) -> data_2.getCounter().compareTo(data_1.getCounter()));
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
	 * Method to return the total number of records in the file.
	 * @return The number of records as an integer.
	 */
	public int getNoOfRecords() 
	{
		return dataList.size();
	}

	/**
	 * Method to get string details of the maximum, minimum and average values for each graph plot.
	 * @return The string details for each graph plot.
	 */
	public String getGraphDetails(LinkedList<Integer> sensorPoints, int sensLow, int sensHigh)
	{
		int runningTotal = 0;
		int noOfDevices = sensorPoints.size();

    	Collections.sort(sensorPoints);
		int maxVal = sensorPoints.getLast();
		int minVal = sensorPoints.getFirst();

		// Iterate over all data lines and add sensor value to running total
 		ListIterator<Integer> listItSens = sensorPoints.listIterator();

		while (listItSens.hasNext()) {
			Integer nextInt = listItSens.next();
			runningTotal += nextInt;
		}

		int avgVal = runningTotal/noOfDevices;

		String graphDetails = "Max: " + Integer.toString(maxVal) + ", Min: " + Integer.toString(minVal) + ", Average: " + Integer.toString(avgVal);

		return graphDetails;

	}

	/**
	 * Method to return the minimum sensor value found from the CSV file.
	 * @return The minimum sensor value.
	 */
	public int getMinVal(int low, int high)
	{
		int minVal = 0;
		Collections.sort(dataList, (DataLine data_1, DataLine data_2) -> data_1.getSensorData().toString().substring(low,high).compareTo(data_2.getSensorData().toString().substring(low,high)));
		try {
			minVal = Integer.parseInt(dataList.getFirst().getSensorData().substring(low,high), 16);
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Error - some maximum values couldn't be calculated as some data has been found in the wrong format.", "Error", JOptionPane.ERROR_MESSAGE);   
		}
		return minVal;
	}

	/**
	 * Method to return the maximum sensor value found from the CSV file.
	 * @return The maximum sensor value.
	 */
	public int getMaxVal(int low, int high)
	{
		int maxVal = 0;
		Collections.sort(dataList, (DataLine data_1, DataLine data_2) -> data_2.getSensorData().toString().substring(low,high).compareTo(data_1.getSensorData().toString().substring(low,high)));
		try {
			maxVal = Integer.parseInt(dataList.getFirst().getSensorData().substring(low,high), 16);
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(new JFrame(), "Error - some maximum values couldn't be calculated as some data has been found in the wrong format.", "Error", JOptionPane.ERROR_MESSAGE);   
		}
		return maxVal;
	}

	/**
	 * Method to return the average sensor value found from the CSV file.
	 * @return The average sensor value.
	 */
	public int getAvgVal(int low, int high)
	{
		int noOfSensors = getNoOfRecords();
		int runningTotal = 0;

		// Iterate over all data lines and add sensor value to running total
 		listIt = dataList.listIterator();

		while (listIt.hasNext()) {
			nextData = listIt.next();
			try {
				runningTotal += Integer.parseInt(nextData.getSensorData().substring(low,high), 16);
			}
			catch (NumberFormatException e) {
				continue;
			}

		}

		int avgVal = runningTotal/noOfSensors;
		return avgVal;
	}

	/**
	 * Method to return a linked list of all the data lines present in the CSV input file for use in output table.
	 * @return The linked list of data lines.
	 */
	public LinkedList<DataLine> getAllData() 
	{
		return dataList;
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