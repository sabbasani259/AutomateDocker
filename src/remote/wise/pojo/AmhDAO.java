/**
 * 
 */
package remote.wise.pojo;

import java.sql.Timestamp;

/**
 * @author roopn5
 *
 */
public class AmhDAO {
	
	private int Transaction_Number;
	private Timestamp Transaction_Timestamp;
	private String Serial_Number;
	private String Created_Timestamp;
	private int Record_Type_Id;
	private String FW_Version_Number;
	private String Last_Updated_Timestamp;
	private int Update_Count;
	private int Segment_ID_TxnDate;
	/**
	 * @return the transaction_Number
	 */
	public int getTransaction_Number() {
		return Transaction_Number;
	}
	/**
	 * @param transaction_Number the transaction_Number to set
	 */
	public void setTransaction_Number(int transaction_Number) {
		Transaction_Number = transaction_Number;
	}
	
	public Timestamp getTransaction_Timestamp() {
		return Transaction_Timestamp;
	}
	public void setTransaction_Timestamp(Timestamp transaction_Timestamp) {
		Transaction_Timestamp = transaction_Timestamp;
	}
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
	 * @return the record_Type_Id
	 */
	public int getRecord_Type_Id() {
		return Record_Type_Id;
	}
	/**
	 * @param record_Type_Id the record_Type_Id to set
	 */
	public void setRecord_Type_Id(int record_Type_Id) {
		Record_Type_Id = record_Type_Id;
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
	
	

}
