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

public class Driver
{
	// Date instance variables
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.UK);
	private static final TimeZone timeZone = TimeZone.getTimeZone("UTC");
    private GregorianCalendar cal;

	public void findFile()
	{
		// A Swing component for file request dialogs! 
		JFileChooser source = new JFileChooser();

		// Use FileNameExtensionFilter to limit what files we want to get (by file extension) 
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma Seperated Files", "csv"); 
		source.setFileFilter(filter);

		// Ask for a file, and check if the user actually selected one!
		if (source.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
		{   
			// Get the file the user selected
			File selectedFile = source.getSelectedFile();

			// Here is where you would kick off the reading/parsing stages 
			String[] dataLine = null;
			String line = "";

			try {
				BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
	            while ((line = reader.readLine()) != null) {

	                // use comma as separator
	                dataLine = line.split(",");
	                System.out.println("Time: " + dataLine[0] + ", Type: " + dataLine[1] + ", Version: " + dataLine[2]
	                	+ ", Counter: " + dataLine[3] + ", Via: " + dataLine[4] + ", Address: " + dataLine[5]
	                	+ ", Status: " + dataLine[6] + ", Sensor Data: " + dataLine[7]);

	                System.out.println(addSecondsToDate(Integer.parseInt(dataLine[0])) + "\n");
	            }
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
		else 
		{
			System.out.println("No file chosen!");
		}
	}

	private String addSecondsToDate(int s)
	{
        cal = new GregorianCalendar(2000,00,01,0,0,0);
        cal.add(Calendar.SECOND, s);
        return (dateFormat.format(cal.getTime()));
	}

	public void showScreen()
	{
		JFrame window = new JFrame();
		JPanel mainPanel = new JPanel();
		GraphComponent graph = new GraphComponent();

		mainPanel.add(graph);

		// further window details
        window.setContentPane(mainPanel);
        window.setTitle("Sensor Data Visualisation");
        window.setSize(700, 700);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocation(200,200);
        window.setResizable(false);
        window.setVisible(true);
	}

	public static void main(String[] args)
	{
		dateFormat.setTimeZone(timeZone);

		// Schedule a job for the event-dispatching thread: creating + showing the GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Driver d = new Driver();
                //d.showScreen();
                d.findFile();

            }
        });
	}
}


