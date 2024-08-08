package remote.wise.service.datacontract;

public class DetailedUsercommunicationReportOutputContract {

	String serialNumber;
	String eventGeneratedTime;
	String alertDesc;
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
	 * @return the alertDesc
	 */
	public String getAlertDesc() {
		return alertDesc;
	}
	/**
	 * @param alertDesc the alertDesc to set
	 */
	public void setAlertDesc(String alertDesc) {
		this.alertDesc = alertDesc;
	}
	
}
