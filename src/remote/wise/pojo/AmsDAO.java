/**
 * 
 */
package remote.wise.pojo;

import java.util.HashMap;

/**
 * @author roopn5
 *
 */
public class AmsDAO {
	
	public String Serial_Number;
	private String Transaction_Timestamp_Log;
	private String Transaction_Timestamp_Evt;
	private String Transaction_Timestamp_Fuel;
	private String Latest_Created_Timestamp;
	private String Fuel_Level;
	private int Latest_Log_Transaction;
	private int Latest_Event_Transaction;
	private int Latest_Fuel_Transaction;
	private int Transaction_Number;
	private String parameters;
	private String Latest_Transaction_Timestamp;
	private String rollOffDate;
	private String account_name;
	private String asset_group_name;
	private HashMap<String,String> TxnData;
	
	
	
	
	/**
	 * @return the asset_group_name
	 */
	public String getAsset_group_name() {
		return asset_group_name;
	}
	/**
	 * @param asset_group_name the asset_group_name to set
	 */
	public void setAsset_group_name(String asset_group_name) {
		this.asset_group_name = asset_group_name;
	}
	/**
	 * @return the rollOffDate
	 */
	public String getRollOffDate() {
		return rollOffDate;
	}
	/**
	 * @param rollOffDate the rollOffDate to set
	 */
	public void setRollOffDate(String rollOffDate) {
		this.rollOffDate = rollOffDate;
	}
	/**
	 * @return the account_name
	 */
	public String getAccount_name() {
		return account_name;
	}
	/**
	 * @param account_name the account_name to set
	 */
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
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
	 * @return the fuel_Level
	 */
	public String getFuel_Level() {
		return Fuel_Level;
	}
	/**
	 * @param fuel_Level the fuel_Level to set
	 */
	public void setFuel_Level(String fuel_Level) {
		Fuel_Level = fuel_Level;
	}
	/**
	 * @return the latest_Log_Transaction
	 */
	public int getLatest_Log_Transaction() {
		return Latest_Log_Transaction;
	}
	/**
	 * @param latest_Log_Transaction the latest_Log_Transaction to set
	 */
	public void setLatest_Log_Transaction(int latest_Log_Transaction) {
		Latest_Log_Transaction = latest_Log_Transaction;
	}
	/**
	 * @return the latest_Event_Transaction
	 */
	public int getLatest_Event_Transaction() {
		return Latest_Event_Transaction;
	}
	/**
	 * @param latest_Event_Transaction the latest_Event_Transaction to set
	 */
	public void setLatest_Event_Transaction(int latest_Event_Transaction) {
		Latest_Event_Transaction = latest_Event_Transaction;
	}
	/**
	 * @return the latest_Fuel_Transaction
	 */
	public int getLatest_Fuel_Transaction() {
		return Latest_Fuel_Transaction;
	}
	/**
	 * @param latest_Fuel_Transaction the latest_Fuel_Transaction to set
	 */
	public void setLatest_Fuel_Transaction(int latest_Fuel_Transaction) {
		Latest_Fuel_Transaction = latest_Fuel_Transaction;
	}
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
	/**
	 * @return the parameters
	 */
	public String getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
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
