package remote.wise.service.datacontract;

public class SMSAlertsRespContract 
{
	private String serialNumber;
	private String cmhr;
	private String smsReceivedTime;
	private String failureReason;
	private int towawayStatus;
	private int highCoolantTempStatus;
	private int lowEngineOilPressureStatus;
	private int waterInFuelStatus;
	private int blockedAirFilterStatus;
	
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
	 * @return the smsReceivedTime
	 */
	public String getSmsReceivedTime() {
		return smsReceivedTime;
	}
	/**
	 * @param smsReceivedTime the smsReceivedTime to set
	 */
	public void setSmsReceivedTime(String smsReceivedTime) {
		this.smsReceivedTime = smsReceivedTime;
	}
	/**
	 * @return the failureReason
	 */
	public String getFailureReason() {
		return failureReason;
	}
	/**
	 * @param failureReason the failureReason to set
	 */
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	/**
	 * @return the towawayStatus
	 */
	public int getTowawayStatus() {
		return towawayStatus;
	}
	/**
	 * @param towawayStatus the towawayStatus to set
	 */
	public void setTowawayStatus(int towawayStatus) {
		this.towawayStatus = towawayStatus;
	}
	/**
	 * @return the highCoolantTempStatus
	 */
	public int getHighCoolantTempStatus() {
		return highCoolantTempStatus;
	}
	/**
	 * @param highCoolantTempStatus the highCoolantTempStatus to set
	 */
	public void setHighCoolantTempStatus(int highCoolantTempStatus) {
		this.highCoolantTempStatus = highCoolantTempStatus;
	}
	/**
	 * @return the lowEngineOilPressureStatus
	 */
	public int getLowEngineOilPressureStatus() {
		return lowEngineOilPressureStatus;
	}
	/**
	 * @param lowEngineOilPressureStatus the lowEngineOilPressureStatus to set
	 */
	public void setLowEngineOilPressureStatus(int lowEngineOilPressureStatus) {
		this.lowEngineOilPressureStatus = lowEngineOilPressureStatus;
	}
	/**
	 * @return the waterInFuelStatus
	 */
	public int getWaterInFuelStatus() {
		return waterInFuelStatus;
	}
	/**
	 * @param waterInFuelStatus the waterInFuelStatus to set
	 */
	public void setWaterInFuelStatus(int waterInFuelStatus) {
		this.waterInFuelStatus = waterInFuelStatus;
	}
	/**
	 * @return the blockedAirFilterStatus
	 */
	public int getBlockedAirFilterStatus() {
		return blockedAirFilterStatus;
	}
	/**
	 * @param blockedAirFilterStatus the blockedAirFilterStatus to set
	 */
	public void setBlockedAirFilterStatus(int blockedAirFilterStatus) {
		this.blockedAirFilterStatus = blockedAirFilterStatus;
	}
	
	
}
