import javax.swing.*;

/**
 * Driver class to run the sensor data visualisation application.
 * 
 * @author Harry Baines
 */
public class Driver
{
	/**
	 * Main method used to run the application and show the UI.
	 * @param args Unused.
	 */
	public static void main(String[] args)
	{
		// Schedule a job for the event-dispatching thread: creating + showing the GUI.
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                MainScreen m = new MainScreen();
                m.displayScreen();
            }
        });
	}
}