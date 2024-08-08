/**
 * CR317:Add VIN city district state in Alert Severity Report
 * CR328:Display the DTC Error code on the Alert Severity Report
 */
package remote.wise.service.datacontract;

/**
 * @author sunayak
 *
 */
public class AlertSeverityReportRespContract {

	private String serialNumber;
	private String dealerName;
	private String customerName;
	private String alertDescription;
	private String latestReceivedTime;
	private String alertSeverity;
	private String zone;
	private String profile;
	private String model;
	private String machineHrs;
	private String pktReceivedDate;
	private String machineRollOffDate;
	private String machineInstallationDate;
	private String customerNumber;
	private String city ;
	private String district;
	private String state; 
	private String dtcCode;
	/**
	 * @return the Customer No
	 */

	public String getCustomerNumber() {
		return customerNumber;
	}

	/**
	 * @param Customer No the Customer No to set
	 */
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	/**
	 * @return the Machine Roll off Date
	 */

	public String getMachineRollOffDate() {
		return machineRollOffDate;
	}

	/**
	 * @param Machine
	 *            Roll off Date the Machine Roll off Date to set
	 */
	public void setMachineRollOffDate(String machineRollOffDate) {
		this.machineRollOffDate = machineRollOffDate;
	}

	/**
	 * @return the Machine Installation Date
	 */
	public String getMachineInstallationDate() {
		return machineInstallationDate;
	}

	/**
	 * @param Machine
	 *            Installation Date the Machine Installation Date to set
	 */
	public void setMachineInstallationDate(String machineInstallationDate) {
		this.machineInstallationDate = machineInstallationDate;
	}

	/**
	 * @return the packetReceived Date
	 */

	public String getPktReceivedDate() {
		return pktReceivedDate;
	}
	/**
	 * @param packetReceived Date the packetReceived Date to set
	 */
	public void setPktReceivedDate(String pktReceivedDate) {
		this.pktReceivedDate = pktReceivedDate;
	}

	/**
	 * @return the Machine Hrs
	 */
	public String getMachineHrs() {
		return machineHrs;
	}

	/**
	 * @param Machine
	 *            Hrs the Machine Hrs to set
	 */
	public void setMachineHrs(String machineHrs) {
		this.machineHrs = machineHrs;
	}

	/**
	 * @return the Model
	 */

	public String getModel() {
		return model;
	}

	/**
	 * @param Model
	 *            the Model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the Profile
	 */

	public String getProfile() {
		return profile;
	}

	/**
	 * @param Profile
	 *            the Profile to set
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	/**
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * @param zone
	 *            the zone to set
	 */
	public void setZone(String zone) {
		this.zone = zone;
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
	 * @return the dealerName
	 */
	public String getDealerName() {
		return dealerName;
	}
	/**
	 * @param dealerName the dealerName to set
	 */
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}
	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	/**
	 * @return the alertDescription
	 */
	public String getAlertDescription() {
		return alertDescription;
	}
	/**
	 * @param alertDescription the alertDescription to set
	 */
	public void setAlertDescription(String alertDescription) {
		this.alertDescription = alertDescription;
	}
	/**
	 * @return the latestReceivedTime
	 */
	public String getLatestReceivedTime() {
		return latestReceivedTime;
	}
	/**
	 * @param latestReceivedTime the latestReceivedTime to set
	 */
	public void setLatestReceivedTime(String latestReceivedTime) {
		this.latestReceivedTime = latestReceivedTime;
	}
	/**
	 * @return the alertSeverity
	 */
	public String getAlertSeverity() {
		return alertSeverity;
	}
	/**
	 * @param alertSeverity the alertSeverity to set
	 */
	public void setAlertSeverity(String alertSeverity) {
		this.alertSeverity = alertSeverity;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDtcCode() {
		return dtcCode;
	}

	public void setDtcCode(String dtcCode) {
		this.dtcCode = dtcCode;
	}

	@Override
	public String toString() {
		return "AlertSeverityReportRespContract [serialNumber=" + serialNumber
				+ ", dealerName=" + dealerName + ", customerName="
				+ customerName + ", alertDescription=" + alertDescription
				+ ", latestReceivedTime=" + latestReceivedTime
				+ ", alertSeverity=" + alertSeverity + ", zone=" + zone
				+ ", profile=" + profile + ", model=" + model + ", machineHrs="
				+ machineHrs + ", pktReceivedDate=" + pktReceivedDate
				+ ", machineRollOffDate=" + machineRollOffDate
				+ ", machineInstallationDate=" + machineInstallationDate
				+ ", customerNumber=" + customerNumber + ", city=" + city
				+ ", district=" + district + ", state=" + state + ", dtcCode="
				+ dtcCode + "]";
	}
	
	
	
}
