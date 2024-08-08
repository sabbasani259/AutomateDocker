package remote.wise.businessentity;

import java.sql.Timestamp;

//DF20131223 - Rajani Nagaraju - To log the details of the SMS that are being sent to KAPSYS
public class SmsLogDetailsEntity extends BaseBusinessEntity
{
	private int smsLoggerId;
	private String serialNumber;
	private String mobileNumber;
	private Timestamp smsSentTime;
	private String smsBody;
	//DF20140105 - Rajani Nagaraju - Capturing Response ID sent from KAPSYS on receival of HTTP message
	private String responseId;
	//DF20150317 - Rajani Nagaraju - To Link the sent SMS wih the corresponding asset event
	private int assetEventId;
	
	//DF20160504 - Rajani Nagaraju  - Required for Tracking SMSs
	private String language;
	private String eventCode;
	private Timestamp eventGeneratedTS;
	private int month;
	private int year;
	
	/**
	 * @return the smsLoggerId
	 */
	public int getSmsLoggerId() {
		return smsLoggerId;
	}
	/**
	 * @param smsLoggerId the smsLoggerId to set
	 */
	public void setSmsLoggerId(int smsLoggerId) {
		this.smsLoggerId = smsLoggerId;
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
	 * @return the smsSentTime
	 */
	public Timestamp getSmsSentTime() {
		return smsSentTime;
	}
	/**
	 * @param smsSentTime the smsSentTime to set
	 */
	public void setSmsSentTime(Timestamp smsSentTime) {
		this.smsSentTime = smsSentTime;
	}
	/**
	 * @return the smsBody
	 */
	public String getSmsBody() {
		return smsBody;
	}
	/**
	 * @param smsBody the smsBody to set
	 */
	public void setSmsBody(String smsBody) {
		this.smsBody = smsBody;
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getEventCode() {
		return eventCode;
	}
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}
	public Timestamp getEventGeneratedTS() {
		return eventGeneratedTS;
	}
	public void setEventGeneratedTS(Timestamp eventGeneratedTS) {
		this.eventGeneratedTS = eventGeneratedTS;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	
}
