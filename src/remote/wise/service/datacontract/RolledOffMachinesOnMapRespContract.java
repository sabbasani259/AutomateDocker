package remote.wise.service.datacontract;


public class RolledOffMachinesOnMapRespContract {
	private String serialNumber;
	private String zone;
	private String dealerCode;
	private String dealerName;
	private String customerBPCode;
	private String customerName;
	private String modelCode;
	private String profile;
	private String rollOffDate;
	private String installationDate;
	private String latitude;
	private String longitude;
	private String machineHours;
	private String fwVersion;
	private String city;
	private String state;
	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(String installationDate) {
		this.installationDate = installationDate;
	}
	
	public String getMachineHours() {
		return machineHours;
	}

	public void setMachineHours(String machineHours) {
		this.machineHours = machineHours;
	}

	public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public RolledOffMachinesOnMapRespContract() {
		super();
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getDealerCode() {
		return dealerCode;
	}
	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public String getCustomerBPCode() {
		return customerBPCode;
	}
	public void setCustomerBPCode(String customerBPCode) {
		this.customerBPCode = customerBPCode;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getModelCode() {
		return modelCode;
	}
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	public String getRollOffDate() {
		return rollOffDate;
	}
	public void setRollOffDate(String rollOffDate) {
		this.rollOffDate = rollOffDate;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}
