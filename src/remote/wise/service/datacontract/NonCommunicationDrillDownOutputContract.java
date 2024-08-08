package remote.wise.service.datacontract;

public class NonCommunicationDrillDownOutputContract {
	private String vin;
	private String zone;
	private String dealer;
	private String customer;
	private String lastCommunicatedDate;
	private String installationDate;
	private String rollOffDate;
	private String renewalStatus;
	private String imsi;
	private String renewalExpiryDate;
	private String machineHours;
	private String model;
	private String profile;
	private String firmware;
	private String vinState;
	private String vinCity;
	private String vinAddress;
	
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getDealer() {
		return dealer;
	}
	public void setDealer(String dealer) {
		this.dealer = dealer;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getLastCommunicatedDate() {
		return lastCommunicatedDate;
	}
	public void setLastCommunicatedDate(String lastCommunicatedDate) {
		this.lastCommunicatedDate = lastCommunicatedDate;
	}
	public String getInstallationDate() {
		return installationDate;
	}
	public void setInstallationDate(String installationDate) {
		this.installationDate = installationDate;
	}
	public String getRollOffDate() {
		return rollOffDate;
	}
	public void setRollOffDate(String rollOffDate) {
		this.rollOffDate = rollOffDate;
	}
	public String getRenewalStatus() {
		return renewalStatus;
	}
	public void setRenewalStatus(String renewalStatus) {
		this.renewalStatus = renewalStatus;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getRenewalExpiryDate() {
		return renewalExpiryDate;
	}
	public void setRenewalExpiryDate(String renewalExpiryDate) {
		this.renewalExpiryDate = renewalExpiryDate;
	}
	public String getMachineHours() {
		return machineHours;
	}
	public void setMachineHours(String machineHours) {
		this.machineHours = machineHours;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getFirmware() {
		return firmware;
	}
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}
	public String getVinState() {
		return vinState;
	}
	public void setVinState(String vinState) {
		this.vinState = vinState;
	}
	public String getVinCity() {
		return vinCity;
	}
	public void setVinCity(String vinCity) {
		this.vinCity = vinCity;
	}
	public String getVinAddress() {
		return vinAddress;
	}
	public void setVinAddress(String vinAddress) {
		this.vinAddress = vinAddress;
	}
	@Override
	public String toString() {
		return "NonCommunicationDrillDownOutputContract [vin=" + vin
				+ ", zone=" + zone + ", dealer=" + dealer + ", customer="
				+ customer + ", lastCommunicatedDate=" + lastCommunicatedDate
				+ ", installationDate=" + installationDate + ", rollOffDate="
				+ rollOffDate + ", renewalStatus=" + renewalStatus + ", imsi="
				+ imsi + ", renewalExpiryDate=" + renewalExpiryDate
				+ ", machineHours="
				+ machineHours + ", model=" + model + ", profile=" + profile
				+ ", firmware=" + firmware + ", vinState=" + vinState
				+ ", vinCity=" + vinCity + ", vinAddress=" + vinAddress + "]";
	}
	
}
