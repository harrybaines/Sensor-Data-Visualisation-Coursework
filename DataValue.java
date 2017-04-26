public class DataValue
{
	// Instance variables
	private int time;
	private String type;
	private String version;
	private String counter;
	private String via;
	private String address;
	private String status;
	private String sensor_data;

	/**
	 * Constructor to initialise an object with data read from the chosen CSV file.
	 * @param time The time the sensor reading was sent, in seconds from 1/1/2000 00:00:00.
	 * @param type The type code to identify what device this is (should always be 0x20).
	 * @param version What software version the device is running.
	 * @param counter A rolling 8-bit, ever increasing number. Used to show how many messages are being missed by the receiver.
	 * @param via Which receiver picked up this device’s transmission.
	 * @param address The address of the transmitter.
	 * @param status The status code of the device, as a bit-packed field - non-zero value = error, each bit in field is different error.
	 * @param sensor_data 10-bytes of sensor data, in hexadecimal.
	 */
	public DataValue(int time, String type, String version, String counter, String via, String address,
		String status, String sensor_data)
	{
		this.time = time;
		this.type = type;
		this.version = version;
		this.counter = counter;
		this.via = via;
		this.address = address;
		this.status = status;
		this.sensor_data = sensor_data;
	}	
 	
 	/**
 	 * Returns the time in seconds the sensor reading was sent.
 	 * @return The time in seconds.
 	 */
 	public int getTime()
 	{
 		return time;
 	}

	/**
 	 * Returns the type code of the device.
 	 * @return The type code as a string.
 	 */
 	public String getType()
 	{
 		return type;
 	}

	/**
 	 * Returns the software version the device is running.
 	 * @return The software version as string.
 	 */
 	public String getVersion()
 	{
 		return version;
 	}

	/**
 	 * Returns the number of messages being missed by the receiver.
 	 * @return The counter value as an integer.
 	 */
 	public String getCounter()
 	{
 		return counter;
 	}

	/**
 	 * Returns which receiver picked up the device's transmission.
 	 * @return The receiver as a string.
 	 */
 	public String getVia()
 	{
 		return via;
 	}

	/**
 	 * Returns the address of the transmitter.
 	 * @return The address as a string.
 	 */
 	public String getAddress()
 	{
 		return address;
 	}

	/**
 	 * Returns the status code of the device.
 	 * @return The status code as a string.
 	 */
 	public String getStatus()
 	{
 		return status;
 	}

	/**
 	 * Returns the 10-bytes of sensor data. 
 	 * @return The sensor data as a string.
 	 */
 	public String getSensorData()
 	{
 		return sensor_data;
 	}
}

