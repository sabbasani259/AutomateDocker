package remote.wise.service.datacontract;

import java.util.List;

public class UserAssetDetailsRespContract 
{
	String serialNumber;
	String assetClassName;
	String assetGroupName;
	String assetTypeName;
	int make;
	String customerName;
	String customerPhoneNumber;
	String customerEmailId;
	String dealerName;
	String dealerPhoneNumber;
	String dealerEmailId;
	List<String> assetCustomGroupName;
	String lifeHours;
	int DealerTenancyId;
	String renewalDate;
	//added by smitha on 26th june 2013...Defect ID 136
	String driverName;
	String driverContactNumber;
	String imeiNumber;
	String simNumber;
	String assetTypeCode;	//CR353.n
	int Subscription; //CR353.n
	String fuelLevel;
	public String getFuelLevel() {
		return fuelLevel;
	}
	public void setFuelLevel(String fuelLevel) {
		this.fuelLevel = fuelLevel;
	}
	/**
	 * @return the imeiNumber
	 */
	public String getImeiNumber() {
		return imeiNumber;
	}
	/**
	 * @param imeiNumber the imeiNumber to set
	 */
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}
	/**
	 * @return the simNumber
	 */
	public String getSimNumber() {
		return simNumber;
	}
	/**
	 * @param simNumber the simNumber to set
	 */
	public void setSimNumber(String simNumber) {
		this.simNumber = simNumber;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverContactNumber() {
		return driverContactNumber;
	}
	public void setDriverContactNumber(String driverContactNumber) {
		this.driverContactNumber = driverContactNumber;
	}
	
	//end
	
	public int getDealerTenancyId() {
		return DealerTenancyId;
	}
	public void setDealerTenancyId(int dealerTenancyId) {
		DealerTenancyId = dealerTenancyId;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getAssetClassName() {
		return assetClassName;
	}
	public void setAssetClassName(String assetClassName) {
		this.assetClassName = assetClassName;
	}
	public String getAssetGroupName() {
		return assetGroupName;
	}
	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}
	public String getAssetTypeName() {
		return assetTypeName;
	}
	public void setAssetTypeName(String assetTypeName) {
		this.assetTypeName = assetTypeName;
	}
	public int getMake() {
		return make;
	}
	public void setMake(int make) {
		this.make = make;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerPhoneNumber() {
		return customerPhoneNumber;
	}
	public void setCustomerPhoneNumber(String customerPhoneNumber) {
		this.customerPhoneNumber = customerPhoneNumber;
	}
	public String getCustomerEmailId() {
		return customerEmailId;
	}
	public void setCustomerEmailId(String customerEmailId) {
		this.customerEmailId = customerEmailId;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public String getDealerPhoneNumber() {
		return dealerPhoneNumber;
	}
	public void setDealerPhoneNumber(String dealerPhoneNumber) {
		this.dealerPhoneNumber = dealerPhoneNumber;
	}
	public String getDealerEmailId() {
		return dealerEmailId;
	}
	public void setDealerEmailId(String dealerEmailId) {
		this.dealerEmailId = dealerEmailId;
	}
	public List<String> getAssetCustomGroupName() {
		return assetCustomGroupName;
	}
	public void setAssetCustomGroupName(List<String> assetCustomGroupName) {
		this.assetCustomGroupName = assetCustomGroupName;
	}
	public String getLifeHours() {
		return lifeHours;
	}
	public void setLifeHours(String lifeHours) {
		this.lifeHours = lifeHours;
	}
	public String getRenewalDate() {
		return renewalDate;
	}
	public void setRenewalDate(String renewalDate) {
		this.renewalDate = renewalDate;
	}
	public String getAssetTypeCode() {
		return assetTypeCode;
	}
	public void setAssetTypeCode(String assetTypeCode) {
		this.assetTypeCode = assetTypeCode;
	}
	public int getSubscription() {
		return Subscription;
	}
	public void setSubscription(int subscription) {
		Subscription = subscription;
	}
	
}
