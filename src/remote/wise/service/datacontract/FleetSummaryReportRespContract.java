
package remote.wise.service.datacontract;

import java.util.List;

public class FleetSummaryReportRespContract 
{
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	private String serialNumber;
	private String machineName;//nickName,TABLE-asset
	
	private double averageFuelConsumption;
	private double fuelused;
	private double fuelUsedInIdle;	
		
	private double powerBandhigh;
	private double powerBandMedium;
	private double powerBandLow;	
	
	private double workingTime;
	private double idleTime;
	private double engineOff;
	
	private double machineHours;//(inPeriod);
	private double totalMachineLifeHours;
	
	private int machineProfile;//asset_Group_Id,TABLE-asset_class_dimension
	private String profile;//asset_Group_Name,TABLE-asset_class_dimension
	
	private int  machineGroupIdList;//CustomassetGroupIdList
	private String machineGroupName;//CustomAssetGroupName
	
	private int modelIdList;
	private String modelName;
	
	private int tenanctIdList;
	private String tenancyName;
	//DefectId:20150216 @ Suprava Dealer As a new parameter Added
	private String dealername;
	/**
	 * @return the dealername
	 */
	public String getDealername() {
		return dealername;
	}
	/**
	 * @param dealername the dealername to set
	 */
	public void setDealername(String dealername) {
		this.dealername = dealername;
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
	 * @return the machineName
	 */
	public String getMachineName() {
		return machineName;
	}
	/**
	 * @param machineName the machineName to set
	 */
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	/**
	 * @return the averageFuelConsumption
	 */
	public double getAverageFuelConsumption() {
		return averageFuelConsumption;
	}
	/**
	 * @param averageFuelConsumption the averageFuelConsumption to set
	 */
	public void setAverageFuelConsumption(double averageFuelConsumption) {
		this.averageFuelConsumption = averageFuelConsumption;
	}
	/**
	 * @return the fuelused
	 */
	public double getFuelused() {
		return fuelused;
	}
	/**
	 * @param fuelused the fuelused to set
	 */
	public void setFuelused(double fuelused) {
		this.fuelused = fuelused;
	}
	/**
	 * @return the fuelUsedInIdle
	 */
	public double getFuelUsedInIdle() {
		return fuelUsedInIdle;
	}
	/**
	 * @param fuelUsedInIdle the fuelUsedInIdle to set
	 */
	public void setFuelUsedInIdle(double fuelUsedInIdle) {
		this.fuelUsedInIdle = fuelUsedInIdle;
	}
	/**
	 * @return the powerBandhigh
	 */
	public double getPowerBandhigh() {
		return powerBandhigh;
	}
	/**
	 * @param powerBandhigh the powerBandhigh to set
	 */
	public void setPowerBandhigh(double powerBandhigh) {
		this.powerBandhigh = powerBandhigh;
	}
	/**
	 * @return the powerBandMedium
	 */
	public double getPowerBandMedium() {
		return powerBandMedium;
	}
	/**
	 * @param powerBandMedium the powerBandMedium to set
	 */
	public void setPowerBandMedium(double powerBandMedium) {
		this.powerBandMedium = powerBandMedium;
	}
	/**
	 * @return the powerBandLow
	 */
	public double getPowerBandLow() {
		return powerBandLow;
	}
	/**
	 * @param powerBandLow the powerBandLow to set
	 */
	public void setPowerBandLow(double powerBandLow) {
		this.powerBandLow = powerBandLow;
	}
	/**
	 * @return the workingTime
	 */
	public double getWorkingTime() {
		return workingTime;
	}
	/**
	 * @param workingTime the workingTime to set
	 */
	public void setWorkingTime(double workingTime) {
		this.workingTime = workingTime;
	}
	/**
	 * @return the idleTime
	 */
	public double getIdleTime() {
		return idleTime;
	}
	/**
	 * @param idleTime the idleTime to set
	 */
	public void setIdleTime(double idleTime) {
		this.idleTime = idleTime;
	}
	/**
	 * @return the engineOff
	 */
	public double getEngineOff() {
		return engineOff;
	}
	/**
	 * @param engineOff the engineOff to set
	 */
	public void setEngineOff(double engineOff) {
		this.engineOff = engineOff;
	}
	/**
	 * @return the machineHours
	 */
	public double getMachineHours() {
		return machineHours;
	}
	/**
	 * @param machineHours the machineHours to set
	 */
	public void setMachineHours(double machineHours) {
		this.machineHours = machineHours;
	}
	/**
	 * @return the totalMachineLifeHours
	 */
	public double getTotalMachineLifeHours() {
		return totalMachineLifeHours;
	}
	/**
	 * @param totalMachineLifeHours the totalMachineLifeHours to set
	 */
	public void setTotalMachineLifeHours(double totalMachineLifeHours) {
		this.totalMachineLifeHours = totalMachineLifeHours;
	}
	/**
	 * @return the machineProfile
	 */
	public int getMachineProfile() {
		return machineProfile;
	}
	/**
	 * @param machineProfile the machineProfile to set
	 */
	public void setMachineProfile(int machineProfile) {
		this.machineProfile = machineProfile;
	}
	/**
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}
	/**
	 * @param profile the profile to set
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}
	/**
	 * @return the machineGroupIdList
	 */
	public int getMachineGroupIdList() {
		return machineGroupIdList;
	}
	/**
	 * @param machineGroupIdList the machineGroupIdList to set
	 */
	public void setMachineGroupIdList(int machineGroupIdList) {
		this.machineGroupIdList = machineGroupIdList;
	}
	/**
	 * @return the machineGroupName
	 */
	public String getMachineGroupName() {
		return machineGroupName;
	}
	/**
	 * @param machineGroupName the machineGroupName to set
	 */
	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}
	/**
	 * @return the modelIdList
	 */
	public int getModelIdList() {
		return modelIdList;
	}
	/**
	 * @param modelIdList the modelIdList to set
	 */
	public void setModelIdList(int modelIdList) {
		this.modelIdList = modelIdList;
	}
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}
	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	/**
	 * @return the tenanctIdList
	 */
	public int getTenanctIdList() {
		return tenanctIdList;
	}
	/**
	 * @param tenanctIdList the tenanctIdList to set
	 */
	public void setTenanctIdList(int tenanctIdList) {
		this.tenanctIdList = tenanctIdList;
	}
	/**
	 * @return the tenancyName
	 */
	public String getTenancyName() {
		return tenancyName;
	}
	/**
	 * @param tenancyName the tenancyName to set
	 */
	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}
	
	
	
}
