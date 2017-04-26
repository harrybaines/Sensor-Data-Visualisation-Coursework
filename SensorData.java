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

public class SensorData
{
	// Calendar/Date instance variables
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.UK);
	private static final TimeZone timeZone = TimeZone.getTimeZone("UTC");
    private GregorianCalendar cal;

    // Data and file variables
    private File selectedFile;
    private JFileChooser source;
    private FileNameExtensionFilter filter;
    private BufferedReader reader;
	private String[] dataLine;
    private String line = "";

    /**
     * Allows the user to open a CSV file of their choice which contains sensor data.
     */
	public void findFile()
	{
		// Swing component = file request dialog
		source = new JFileChooser();
		filter = new FileNameExtensionFilter("Comma Seperated Files", "csv"); 
		source.setFileFilter(filter);

		if (source.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
		{   
			selectedFile = source.getSelectedFile();
			try 
			{
				reader = new BufferedReader(new FileReader(selectedFile));
	            while ((line = reader.readLine()) != null) 
	            {
	                // Comma = separator
	                dataLine = line.split(",");
	                System.out.println("Time: " + dataLine[0] + ", Type: " + dataLine[1] + ", Version: " + dataLine[2]
	                	+ ", Counter: " + dataLine[3] + ", Via: " + dataLine[4] + ", Address: " + dataLine[5]
	                	+ ", Status: " + dataLine[6] + ", Sensor Data: " + dataLine[7]);

	                System.out.println(addSecondsToDate(Integer.parseInt(dataLine[0])) + "\n");
	            }
			}
			catch (FileNotFoundException e) {
            	e.printStackTrace();
			}
       		catch (IOException e) {
            	e.printStackTrace();
       		}
        }
		else 
			System.out.println("No file chosen!");
	}

	/**
	 * Adds a specified amount of seconds to the date set at the year 2000.
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
		dateFormat.setTimeZone(timeZone);

		// Schedule a job for the event-dispatching thread: creating + showing the GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SensorData d = new SensorData();
                HomeScreen h = new HomeScreen();
                d.findFile();
            }
        });
	}
}
