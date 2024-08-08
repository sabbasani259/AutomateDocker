/**
 * 
 */
package remote.wise.pojo;

import java.util.HashMap;

/**
 * @author ROOPN5
 *
 */
public class AMSDoc_DAO {
	private String Serial_Number;
	private String Transaction_Timestamp_Log;
	private String Transaction_Timestamp_Evt;
	private String Transaction_Timestamp_Fuel;
	private String Latest_Created_Timestamp;
	private String Latest_Transaction_Timestamp;
	private HashMap<String,String> Events;
	private HashMap<String,String> TxnData;
	/**
	 * @return the serial_Number
	 */
	public String getSerial_Number() {
		return Serial_Number;
	}
	/**
	 * @param serial_Number the serial_Number to set
	 */
	public void setSerial_Number(String serial_Number) {
		Serial_Number = serial_Number;
	}
	/**
	 * @return the transaction_Timestamp_Log
	 */
	public String getTransaction_Timestamp_Log() {
		return Transaction_Timestamp_Log;
	}
	/**
	 * @param transaction_Timestamp_Log the transaction_Timestamp_Log to set
	 */
	public void setTransaction_Timestamp_Log(String transaction_Timestamp_Log) {
		Transaction_Timestamp_Log = transaction_Timestamp_Log;
	}
	/**
	 * @return the transaction_Timestamp_Evt
	 */
	public String getTransaction_Timestamp_Evt() {
		return Transaction_Timestamp_Evt;
	}
	/**
	 * @param transaction_Timestamp_Evt the transaction_Timestamp_Evt to set
	 */
	public void setTransaction_Timestamp_Evt(String transaction_Timestamp_Evt) {
		Transaction_Timestamp_Evt = transaction_Timestamp_Evt;
	}
	/**
	 * @return the transaction_Timestamp_Fuel
	 */
	public String getTransaction_Timestamp_Fuel() {
		return Transaction_Timestamp_Fuel;
	}
	/**
	 * @param transaction_Timestamp_Fuel the transaction_Timestamp_Fuel to set
	 */
	public void setTransaction_Timestamp_Fuel(String transaction_Timestamp_Fuel) {
		Transaction_Timestamp_Fuel = transaction_Timestamp_Fuel;
	}
	/**
	 * @return the latest_Created_Timestamp
	 */
	public String getLatest_Created_Timestamp() {
		return Latest_Created_Timestamp;
	}
	/**
	 * @param latest_Created_Timestamp the latest_Created_Timestamp to set
	 */
	public void setLatest_Created_Timestamp(String latest_Created_Timestamp) {
		Latest_Created_Timestamp = latest_Created_Timestamp;
	}
	/**
	 * @return the latest_Transaction_Timestamp
	 */
	public String getLatest_Transaction_Timestamp() {
		return Latest_Transaction_Timestamp;
	}
	/**
	 * @param latest_Transaction_Timestamp the latest_Transaction_Timestamp to set
	 */
	public void setLatest_Transaction_Timestamp(String latest_Transaction_Timestamp) {
		Latest_Transaction_Timestamp = latest_Transaction_Timestamp;
	}
	/**
	 * @return the events
	 */
	public HashMap<String, String> getEvents() {
		return Events;
	}
	/**
	 * @param events the events to set
	 */
	public void setEvents(HashMap<String, String> events) {
		Events = events;
	}
	/**
	 * @return the txnData
	 */
	public HashMap<String, String> getTxnData() {
		return TxnData;
	}
	/**
	 * @param txnData the txnData to set
	 */
	public void setTxnData(HashMap<String, String> txnData) {
		TxnData = txnData;
	}
	
	

}
