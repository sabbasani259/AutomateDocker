package remote.wise.service.datacontract;

public class AlertSummaryDetailedRespContract {

	String serialNumber;
	String eventDescription;
	String eventSeverity;
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
	 * @return the eventDescription
	 */
	public String getEventDescription() {
		return eventDescription;
	}
	/**
	 * @param eventDescription the eventDescription to set
	 */
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}
	/**
	 * @return the eventSeverity
	 */
	public String getEventSeverity() {
		return eventSeverity;
	}
	/**
	 * @param eventSeverity the eventSeverity to set
	 */
	public void setEventSeverity(String eventSeverity) {
		this.eventSeverity = eventSeverity;
	}
	
}
