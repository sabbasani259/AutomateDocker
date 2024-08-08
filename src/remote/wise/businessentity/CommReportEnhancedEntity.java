package remote.wise.businessentity;

import java.sql.Timestamp;
import java.util.Date;

public class CommReportEnhancedEntity {
	/*private int primary_owner_id;
	private String  nick_name,description, chasisNumber;
	private Timestamp purchase_date,install_date;
	private boolean active_status;*/
	
	private String serialNumber;
	private int dealerAccountId;
	private String dealerCode;
	private String dealerName;
	private String city;
	private String state;
	private String owner;
	private String tmh;
	private String version;
	private Timestamp pktCreatedTS;
	private Timestamp pktRecdTS;
	private Timestamp rollOffDate;
	private String deviceStatus;
	private String lat;
	private String lon;
	private String simNo;
	private Timestamp installedDate;
	private Timestamp dateSys;
	private int jobId;
	private String BPCode;
	private String renewalFlag;
	private Timestamp renewalDate;
	private String renewState;
	private String country;
	private String countryCode;
	private String plant;
	private String customerCode;
	private String customerName;
	private String zonalCode;
	private String zone;
	private String regionCode;
	private String model;
	private String profile;
	private String extendedWarranty;
	private String mobile;
	private String communicatingCity;
	private String communicatingState;
	private String communicatingAddress;
	private Timestamp lastUpdatedTimestamp;
	private Date adressLastUpdated;
	private String commCity;
	private String commState;
	private String commAddress;
	private String commDistrict;
	private String vdCity;
	private String vdState;
	private String customerMobile;
	
	public String getCustomerMobile() {
		return customerMobile;
	}
	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public int getDealerAccountId() {
		return dealerAccountId;
	}
	public void setDealerAccountId(int dealerAccountId) {
		this.dealerAccountId = dealerAccountId;
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
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getTmh() {
		return tmh;
	}
	public void setTmh(String tmh) {
		this.tmh = tmh;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Timestamp getPktCreatedTS() {
		return pktCreatedTS;
	}
	public void setPktCreatedTS(Timestamp pktCreatedTS) {
		this.pktCreatedTS = pktCreatedTS;
	}
	public Timestamp getPktRecdTS() {
		return pktRecdTS;
	}
	public void setPktRecdTS(Timestamp pktRecdTS) {
		this.pktRecdTS = pktRecdTS;
	}
	public Timestamp getRollOffDate() {
		return rollOffDate;
	}
	public void setRollOffDate(Timestamp rollOffDate) {
		this.rollOffDate = rollOffDate;
	}
	public String getDeviceStatus() {
		return deviceStatus;
	}
	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getSimNo() {
		return simNo;
	}
	public void setSimNo(String simNo) {
		this.simNo = simNo;
	}
	public Timestamp getInstalledDate() {
		return installedDate;
	}
	public void setInstalledDate(Timestamp installedDate) {
		this.installedDate = installedDate;
	}
	public Timestamp getDateSys() {
		return dateSys;
	}
	public void setDateSys(Timestamp dateSys) {
		this.dateSys = dateSys;
	}
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public String getBPCode() {
		return BPCode;
	}
	public void setBPCode(String bPCode) {
		BPCode = bPCode;
	}
	public String getRenewalFlag() {
		return renewalFlag;
	}
	public void setRenewalFlag(String renewalFlag) {
		this.renewalFlag = renewalFlag;
	}
	public Timestamp getRenewalDate() {
		return renewalDate;
	}
	public void setRenewalDate(Timestamp renewalDate) {
		this.renewalDate = renewalDate;
	}
	public String getRenewState() {
		return renewState;
	}
	public void setRenewState(String renewState) {
		this.renewState = renewState;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getPlant() {
		return plant;
	}
	public void setPlant(String plant) {
		this.plant = plant;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getZonalCode() {
		return zonalCode;
	}
	public void setZonalCode(String zonalCode) {
		this.zonalCode = zonalCode;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
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
	public String getExtendedWarranty() {
		return extendedWarranty;
	}
	public void setExtendedWarranty(String extendedWarranty) {
		this.extendedWarranty = extendedWarranty;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCommunicatingCity() {
		return communicatingCity;
	}
	public void setCommunicatingCity(String communicatingCity) {
		this.communicatingCity = communicatingCity;
	}
	public String getCommunicatingState() {
		return communicatingState;
	}
	public void setCommunicatingState(String communicatingState) {
		this.communicatingState = communicatingState;
	}
	public String getCommunicatingAddress() {
		return communicatingAddress;
	}
	public void setCommunicatingAddress(String communicatingAddress) {
		this.communicatingAddress = communicatingAddress;
	}
	public Timestamp getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}
	public void setLastUpdatedTimestamp(Timestamp lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}
	public Date getAdressLastUpdated() {
		return adressLastUpdated;
	}
	public void setAdressLastUpdated(Date adressLastUpdated) {
		this.adressLastUpdated = adressLastUpdated;
	}
	public String getCommCity() {
		return commCity;
	}
	public void setCommCity(String commCity) {
		this.commCity = commCity;
	}
	public String getCommState() {
		return commState;
	}
	public void setCommState(String commState) {
		this.commState = commState;
	}
	public String getCommAddress() {
		return commAddress;
	}
	public void setCommAddress(String commAddress) {
		this.commAddress = commAddress;
	}
	public String getCommDistrict() {
		return commDistrict;
	}
	public void setCommDistrict(String commDistrict) {
		this.commDistrict = commDistrict;
	}
	public String getVdCity() {
		return vdCity;
	}
	public void setVdCity(String vdCity) {
		this.vdCity = vdCity;
	}
	public String getVdState() {
		return vdState;
	}
	public void setVdState(String vdState) {
		this.vdState = vdState;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
}
