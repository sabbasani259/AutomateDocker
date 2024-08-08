package remote.wise.service.datacontract;

public class SOSAlertResponseContract {

	private String serialNumber;
	private String profile;
	private String model;
	private String customerName;
	private String customerMobile;
	private String dealerName;
	private String zone;
	private String commState;
	private String commDistrict;
	private String commCity;
	private String alert;
	private String alertCategory;
	private String alertGenerationTime;
	private String alertClosureTime;
	private String resolutionTime;
	private String cMH;
	private String alertDescription;
	private String alertSeverity;
	private String installedDate;
	private String commAddress;
	private String category;
	private String comments;
	private String partitionKey;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerMobile() {
		return customerMobile;
	}
	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getCommState() {
		return commState;
	}
	public void setCommState(String commState) {
		this.commState = commState;
	}
	public String getCommDistrict() {
		return commDistrict;
	}
	public void setCommDistrict(String commDistrict) {
		this.commDistrict = commDistrict;
	}
	public String getCommCity() {
		return commCity;
	}
	public void setCommCity(String commCity) {
		this.commCity = commCity;
	}
	public String getAlert() {
		return alert;
	}
	public void setAlert(String alert) {
		this.alert = alert;
	}
	public String getAlertCategory() {
		return alertCategory;
	}
	public void setAlertCategory(String alertCategory) {
		this.alertCategory = alertCategory;
	}
	public String getAlertGenerationTime() {
		return alertGenerationTime;
	}
	public void setAlertGenerationTime(String alertGenerationTime) {
		this.alertGenerationTime = alertGenerationTime;
	}
	public String getAlertClosureTime() {
		return alertClosureTime;
	}
	public void setAlertClosureTime(String alertClosureTime) {
		this.alertClosureTime = alertClosureTime;
	}
	public String getResolutionTime() {
		return resolutionTime;
	}
	public void setResolutionTime(String resolutionTime) {
		this.resolutionTime = resolutionTime;
	}
	public String getcMH() {
		return cMH;
	}
	public void setcMH(String cMH) {
		this.cMH = cMH;
	}
	public String getAlertDescription() {
		return alertDescription;
	}
	public void setAlertDescription(String alertDescription) {
		this.alertDescription = alertDescription;
	}
	public String getAlertSeverity() {
		return alertSeverity;
	}
	public void setAlertSeverity(String alertSeverity) {
		this.alertSeverity = alertSeverity;
	}
	public String getInstalledDate() {
		return installedDate;
	}
	public void setInstalledDate(String installedDate) {
		this.installedDate = installedDate;
	}
	public String getCommAddress() {
		return commAddress;
	}
	public void setCommAddress(String commAddress) {
		this.commAddress = commAddress;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getPartitionKey() {
		return partitionKey;
	}
	public void setPartitionKey(String partitionKey) {
		this.partitionKey = partitionKey;
	}	
	
	@Override
	public String toString() {
		return "SOSAlertResponseContract [serialNumber=" + serialNumber + ", profile=" + profile + ", model=" + model
				+ ", customerName=" + customerName + ", customerMobile=" + customerMobile + ", dealerName=" + dealerName
				+ ", zone=" + zone + ", commState=" + commState + ", commDistrict=" + commDistrict + ", commCity="
				+ commCity + ", alert=" + alert + ", alertCategory=" + alertCategory + ", alertGenerationTime="
				+ alertGenerationTime + ", alertClosureTime=" + alertClosureTime + ", resolutionTime=" + resolutionTime
				+ ", cMH=" + cMH + ", alertDescription=" + alertDescription + ", alertSeverity=" + alertSeverity
				+ ", installedDate=" + installedDate + ", commAddress=" + commAddress + ", category=" + category
				+ ", comments=" + comments + ", partitionKey=" + partitionKey + "]";
	}
}
