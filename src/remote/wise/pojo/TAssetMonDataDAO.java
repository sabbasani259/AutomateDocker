/**
 * 
 */
package remote.wise.pojo;

import java.util.HashMap;

/**
 * @author ROOPN5
 *
 */
public class TAssetMonDataDAO {
	private String Serial_Number;
	private String Transaction_Timestamp;
	private String Created_Timestamp;
	private String Last_Updated_Timestamp;
	private HashMap<String,String> Message_ID;
	private HashMap<String,String> Events;
	private HashMap<String,String> TxnData;
	private int Segment_ID_TxnDate;
	private String FW_Version_Number;
	private int Update_Count;
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
	 * @return the transaction_Timestamp
	 */
	public String getTransaction_Timestamp() {
		return Transaction_Timestamp;
	}
	/**
	 * @param transaction_Timestamp the transaction_Timestamp to set
	 */
	public void setTransaction_Timestamp(String transaction_Timestamp) {
		Transaction_Timestamp = transaction_Timestamp;
	}
	/**
	 * @return the created_Timestamp
	 */
	public String getCreated_Timestamp() {
		return Created_Timestamp;
	}
	/**
	 * @param created_Timestamp the created_Timestamp to set
	 */
	public void setCreated_Timestamp(String created_Timestamp) {
		Created_Timestamp = created_Timestamp;
	}
	/**
	 * @return the last_Updated_Timestamp
	 */
	public String getLast_Updated_Timestamp() {
		return Last_Updated_Timestamp;
	}
	/**
	 * @param last_Updated_Timestamp the last_Updated_Timestamp to set
	 */
	public void setLast_Updated_Timestamp(String last_Updated_Timestamp) {
		Last_Updated_Timestamp = last_Updated_Timestamp;
	}
	/**
	 * @return the message_ID
	 */
	public HashMap<String, String> getMessage_ID() {
		return Message_ID;
	}
	/**
	 * @param message_ID the message_ID to set
	 */
	public void setMessage_ID(HashMap<String, String> message_ID) {
		Message_ID = message_ID;
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
	/**
	 * @return the segment_ID_TxnDate
	 */
	public int getSegment_ID_TxnDate() {
		return Segment_ID_TxnDate;
	}
	/**
	 * @param segment_ID_TxnDate the segment_ID_TxnDate to set
	 */
	public void setSegment_ID_TxnDate(int segment_ID_TxnDate) {
		Segment_ID_TxnDate = segment_ID_TxnDate;
	}
	/**
	 * @return the fW_Version_Number
	 */
	public String getFW_Version_Number() {
		return FW_Version_Number;
	}
	/**
	 * @param fW_Version_Number the fW_Version_Number to set
	 */
	public void setFW_Version_Number(String fW_Version_Number) {
		FW_Version_Number = fW_Version_Number;
	}
	/**
	 * @return the update_Count
	 */
	public int getUpdate_Count() {
		return Update_Count;
	}
	/**
	 * @param update_Count the update_Count to set
	 */
	public void setUpdate_Count(int update_Count) {
		Update_Count = update_Count;
	}
	

}
