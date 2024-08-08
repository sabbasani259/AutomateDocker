package remote.wise.service.datacontract;

import java.util.HashMap;

public class PricolTransactionDetailRespContract 
{
	String transactionTimeStamp;
	String serialNumber;
	HashMap<String, String> transactionData;
	
	/**
	 * @return the transactionTimeStamp
	 */
	public String getTransactionTimeStamp() {
		return transactionTimeStamp;
	}
	/**
	 * @param transactionTimeStamp the transactionTimeStamp to set
	 */
	public void setTransactionTimeStamp(String transactionTimeStamp) {
		this.transactionTimeStamp = transactionTimeStamp;
	}
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the transactionData
	 */
	public HashMap<String, String> getTransactionData() {
		return transactionData;
	}
	/**
	 * @param transactionData the transactionData to set
	 */
	public void setTransactionData(HashMap<String, String> transactionData) {
		this.transactionData = transactionData;
	}
	
	
}
