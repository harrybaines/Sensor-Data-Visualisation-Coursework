/**
 * A class to extract and store all relevant variables from string values read from sensors.
 * The string values being read are from the chosen CSV file.
 * This class allows easy data access to individual data properties.
 *
 * @author Harry Baines
 */
public class DataLine {

	// Instance variables - all fields of a data line in a CSV file
	private String time;
	private String type;
	private String version;
	private String counter;
	private String via;
	private String address;
	private String status;
	private String sensor_data;
	private String date_obtained;

	/**
	 * Constructor to initialise an object with data read from the chosen CSV file.
	 * The date the data line was obtained will be calculated and stored in each instance for later reference.
	 *
	 * @param time The time the sensor reading was sent as a string.
	 * @param type The type code to identify what device this is (should always be 0x20).
	 * @param version What software version the device is running.
	 * @param counter A rolling 8-bit, ever increasing number. Used to show how many messages are being missed by the receiver.
	 * @param via Which receiver picked up this device’s transmission.
	 * @param address The address of the transmitter.
	 * @param status The status code of the device, as a bit-packed field - non-zero value = error, each bit in field is different error.
	 * @param sensor_data Sensor data 10-bytes long in hexadecimal.
	 * @param date_obtained The date the data was obtained.
	 */
	public DataLine(String time, String type, String version, String counter, String via, String address,
		String status, String sensor_data, String date_obtained) {
		this.time = time;
		this.type = type;
		this.version = version;
		this.counter = counter;
		this.via = via;
		this.address = address;
		this.status = status;
		this.sensor_data = sensor_data;
		this.date_obtained = date_obtained;
	}	
 	
 	/**
 	 * Returns the time in seconds the sensor reading was sent.
 	 * @return The time in seconds.
 	 */
 	public int getTime() {
 		return (Integer.parseInt(time));
 	}

	/**
 	 * Returns the type code of the device.
 	 * @return The type code as a string.
 	 */
 	public String getType() {
 		return type;
 	}

	/**
 	 * Returns the software version the device is running.
 	 * @return The software version as string.
 	 */
 	public String getVersion() {
 		return version;
 	}

	/**
 	 * Returns the number of messages being missed by the receiver.
 	 * @return The counter value as a string.
 	 */
 	public String getCounter() {
 		return counter;
 	}

	/**
 	 * Returns which receiver picked up the device's transmission.
 	 * @return The receiver as a string.
 	 */
 	public String getVia() {
 		return via;
 	}

	/**
 	 * Returns the address of the transmitter.
 	 * @return The address as a string.
 	 */
 	public String getAddress() {
 		return address;
 	}

	/**
 	 * Returns the status code of the device.
 	 * @return The status code as a string.
 	 */
 	public String getStatus() {
 		return status;
 	}

	/**
 	 * Returns the 10-bytes of sensor data. 
 	 * @return The sensor data as a string.
 	 */
 	public String getSensorData() {
 		return sensor_data;
 	}

 	/**
 	 * Returns the date the data value was obtained.
 	 * @return The date as a string.
 	 */
 	public String getDateObtained() {
 		return date_obtained;
 	}

 	/**
 	 * Returns the full string value containing all details about the data line
 	 * @return The full string information.
 	 */
 	public String getStringLine() {
 		return (time + " " + type + " " + version + " " + counter + " " + via + " " + address + " " + status + " " + sensor_data + " " + date_obtained);
 	}
}