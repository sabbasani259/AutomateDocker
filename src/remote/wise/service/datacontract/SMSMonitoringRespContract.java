package remote.wise.service.datacontract;

public class SMSMonitoringRespContract 
{
	private String serialNumber;
	private String mobileNumber;
	private String eventName;
	private String smsSentTimestamp;
	private String responseId;
	
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
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}
	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}
	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	/**
	 * @return the smsSentTimestamp
	 */
	public String getSmsSentTimestamp() {
		return smsSentTimestamp;
	}
	/**
	 * @param smsSentTimestamp the smsSentTimestamp to set
	 */
	public void setSmsSentTimestamp(String smsSentTimestamp) {
		this.smsSentTimestamp = smsSentTimestamp;
	}
	/**
	 * @return the responseId
	 */
	public String getResponseId() {
		return responseId;
	}
	/**
	 * @param responseId the responseId to set
	 */
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
	
	
}
