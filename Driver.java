import java.io.*;
import java.util.Scanner;
import java.awt.*;
import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Driver
{
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
	            }
    //         	Timestamp stamp = new Timestamp(zeroPointTime);
				// Date date = new Date(stamp.getTime());
			 //    System.out.println(date);


	            Calendar cal = Calendar.getInstance();
	            cal.set(Calendar.MONTH, Calendar.JANUARY);
	            cal.set(Calendar.DAY_OF_MONTH, 1);
	            cal.set(Calendar.YEAR, 2000);
	            cal.set(Calendar.HOUR_OF_DAY, 0);
	            cal.set(Calendar.MINUTE, 1);
	            cal.set(Calendar.SECOND, 0);

		        System.out.println("Now: " + cal.getTime());

		        dataLine[0] = "1";

		        int d = Integer.parseInt(dataLine[0]);
		        cal.add(Calendar.SECOND, d-3600);
		        System.out.println("Later: " + cal.getTime());


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


