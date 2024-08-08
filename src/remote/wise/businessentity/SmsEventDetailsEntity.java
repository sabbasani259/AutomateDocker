package remote.wise.businessentity;

import java.sql.Timestamp;

public class SmsEventDetailsEntity extends BaseBusinessEntity
{
	private int smsEventId;
	private String serialNumber;
	private int gprsNotAvailable;
	private int grpsServiceNotAllowed;
	private int gprsServerCommFailed;
	private String cmhr;
	private String batteryVoltage;
	private String internalBatteryLevel;
	private Timestamp eventGeneratedTime;
	
	
	/**
	 * @return the smsEventId
	 */
	public int getSmsEventId() {
		return smsEventId;
	}
	/**
	 * @param smsEventId the smsEventId to set
	 */
	public void setSmsEventId(int smsEventId) {
		this.smsEventId = smsEventId;
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
	 * @return the gprsNotAvailable
	 */
	public int getGprsNotAvailable() {
		return gprsNotAvailable;
	}
	/**
	 * @param gprsNotAvailable the gprsNotAvailable to set
	 */
	public void setGprsNotAvailable(int gprsNotAvailable) {
		this.gprsNotAvailable = gprsNotAvailable;
	}
	/**
	 * @return the grpsServiceNotAllowed
	 */
	public int getGrpsServiceNotAllowed() {
		return grpsServiceNotAllowed;
	}
	/**
	 * @param grpsServiceNotAllowed the grpsServiceNotAllowed to set
	 */
	public void setGrpsServiceNotAllowed(int grpsServiceNotAllowed) {
		this.grpsServiceNotAllowed = grpsServiceNotAllowed;
	}
	/**
	 * @return the gprsServerCommFailed
	 */
	public int getGprsServerCommFailed() {
		return gprsServerCommFailed;
	}
	/**
	 * @param gprsServerCommFailed the gprsServerCommFailed to set
	 */
	public void setGprsServerCommFailed(int gprsServerCommFailed) {
		this.gprsServerCommFailed = gprsServerCommFailed;
	}
	/**
	 * @return the cmhr
	 */
	public String getCmhr() {
		return cmhr;
	}
	/**
	 * @param cmhr the cmhr to set
	 */
	public void setCmhr(String cmhr) {
		this.cmhr = cmhr;
	}
	/**
	 * @return the batteryVoltage
	 */
	public String getBatteryVoltage() {
		return batteryVoltage;
	}
	/**
	 * @param batteryVoltage the batteryVoltage to set
	 */
	public void setBatteryVoltage(String batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}
	/**
	 * @return the internalBatteryLevel
	 */
	public String getInternalBatteryLevel() {
		return internalBatteryLevel;
	}
	/**
	 * @param internalBatteryLevel the internalBatteryLevel to set
	 */
	public void setInternalBatteryLevel(String internalBatteryLevel) {
		this.internalBatteryLevel = internalBatteryLevel;
	}
	/**
	 * @return the eventGeneratedTime
	 */
	public Timestamp getEventGeneratedTime() {
		return eventGeneratedTime;
	}
	/**
	 * @param eventGeneratedTime the eventGeneratedTime to set
	 */
	public void setEventGeneratedTime(Timestamp eventGeneratedTime) {
		this.eventGeneratedTime = eventGeneratedTime;
	}
	
	
}
