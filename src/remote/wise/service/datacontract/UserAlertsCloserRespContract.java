/**
 * 
 */
package remote.wise.service.datacontract;

/**
 * @author sunayak
 *
 */
public class UserAlertsCloserRespContract {

	private String serialNumber;
	private String loginID;
	/**
	 * @return the loginID
	 */
	public String getLoginID() {
		return loginID;
	}
	/**
	 * @param loginID the loginID to set
	 */
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	private String jobCardNumber;
	private String servicedDate;
	private String messageId;
	private String eventGeneratedTime;
	/**
	 * @return the eventGeneratedTime
	 */
	public String getEventGeneratedTime() {
		return eventGeneratedTime;
	}
	/**
	 * @param eventGeneratedTime the eventGeneratedTime to set
	 */
	public void setEventGeneratedTime(String eventGeneratedTime) {
		this.eventGeneratedTime = eventGeneratedTime;
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
	 * @return the jobCardNumber
	 */
	public String getJobCardNumber() {
		return jobCardNumber;
	}
	/**
	 * @param jobCardNumber the jobCardNumber to set
	 */
	public void setJobCardNumber(String jobCardNumber) {
		this.jobCardNumber = jobCardNumber;
	}
	/**
	 * @return the servicedDate
	 */
	public String getServicedDate() {
		return servicedDate;
	}
	/**
	 * @param servicedDate the servicedDate to set
	 */
	public void setServicedDate(String servicedDate) {
		this.servicedDate = servicedDate;
	}
	
	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
}
