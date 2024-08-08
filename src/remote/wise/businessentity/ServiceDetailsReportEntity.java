package remote.wise.businessentity;

import java.sql.Timestamp;

public class ServiceDetailsReportEntity extends BaseBusinessEntity
{
	private String serialNumber;
	private String serviceScheduleName;
	private String lastServiceName;
	private Timestamp lastServiceDate;
	private String lastServiceHours;
	private String nextServiceName;
	private Timestamp nextServiceDate;
	private String nextServiceHours;
	private String totalEngineHours;
	private Timestamp lastUpdated;
	private int eventId;
	
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
	 * @return the serviceScheduleName
	 */
	public String getServiceScheduleName() {
		return serviceScheduleName;
	}
	/**
	 * @param serviceScheduleName the serviceScheduleName to set
	 */
	public void setServiceScheduleName(String serviceScheduleName) {
		this.serviceScheduleName = serviceScheduleName;
	}
	/**
	 * @return the lastServiceName
	 */
	public String getLastServiceName() {
		return lastServiceName;
	}
	/**
	 * @param lastServiceName the lastServiceName to set
	 */
	public void setLastServiceName(String lastServiceName) {
		this.lastServiceName = lastServiceName;
	}
	/**
	 * @return the lastServiceDate
	 */
	public Timestamp getLastServiceDate() {
		return lastServiceDate;
	}
	/**
	 * @param lastServiceDate the lastServiceDate to set
	 */
	public void setLastServiceDate(Timestamp lastServiceDate) {
		this.lastServiceDate = lastServiceDate;
	}
	/**
	 * @return the lastServiceHours
	 */
	public String getLastServiceHours() {
		return lastServiceHours;
	}
	/**
	 * @param lastServiceHours the lastServiceHours to set
	 */
	public void setLastServiceHours(String lastServiceHours) {
		this.lastServiceHours = lastServiceHours;
	}
	/**
	 * @return the nextServiceName
	 */
	public String getNextServiceName() {
		return nextServiceName;
	}
	/**
	 * @param nextServiceName the nextServiceName to set
	 */
	public void setNextServiceName(String nextServiceName) {
		this.nextServiceName = nextServiceName;
	}
	/**
	 * @return the nextServiceDate
	 */
	public Timestamp getNextServiceDate() {
		return nextServiceDate;
	}
	/**
	 * @param nextServiceDate the nextServiceDate to set
	 */
	public void setNextServiceDate(Timestamp nextServiceDate) {
		this.nextServiceDate = nextServiceDate;
	}
	/**
	 * @return the nextServiceHours
	 */
	public String getNextServiceHours() {
		return nextServiceHours;
	}
	/**
	 * @param nextServiceHours the nextServiceHours to set
	 */
	public void setNextServiceHours(String nextServiceHours) {
		this.nextServiceHours = nextServiceHours;
	}
	/**
	 * @return the totalEngineHours
	 */
	public String getTotalEngineHours() {
		return totalEngineHours;
	}
	/**
	 * @param totalEngineHours the totalEngineHours to set
	 */
	public void setTotalEngineHours(String totalEngineHours) {
		this.totalEngineHours = totalEngineHours;
	}
	/**
	 * @return the lastUpdated
	 */
	public Timestamp getLastUpdated() {
		return lastUpdated;
	}
	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	/**
	 * @return the eventId
	 */
	public int getEventId() {
		return eventId;
	}
	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	
	
}
