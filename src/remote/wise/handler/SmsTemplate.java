package remote.wise.handler;

import java.io.Serializable;
import java.util.List;

public class SmsTemplate implements Serializable
{
	//DefectID:  - 1288 - Rajani Nagaraju - Custom SMS implementation
	List<String> to;
	//DefectID:  - 1288 - Rajani Nagaraju - Custom SMS implementation
	List<String> msgBody;
	//DF20150317 - Rajani Nagaraju - To Link the sent SMS with the corresponding asset event
	int assetEventId;
	
	//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
	String serialNumber;
	String transactionTime;
	
	
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	public List<String> getMsgBody() {
		return msgBody;
	}
	public void setMsgBody(List<String> msgBody) {
		this.msgBody = msgBody;
	}
	
	//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
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
	 * @return the transactionTime
	 */
	public String getTransactionTime() {
		return transactionTime;
	}
	/**
	 * @param transactionTime the transactionTime to set
	 */
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	/**
	 * @return the assetEventId
	 */
	public int getAssetEventId() {
		return assetEventId;
	}
	/**
	 * @param assetEventId the assetEventId to set
	 */
	public void setAssetEventId(int assetEventId) {
		this.assetEventId = assetEventId;
	}
	
	
}
