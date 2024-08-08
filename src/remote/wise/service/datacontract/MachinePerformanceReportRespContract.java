package remote.wise.service.datacontract;

public class MachinePerformanceReportRespContract 
{
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	//Utilization
	private double engineOff;//Engine_Off_Hours
	private double engineOn;//Machine_Hours
	private double workingTime;//Working_Time
	private double idleTime;//Idle_Hours
	
	//Productivity
	private double powerBandLow;
	private double powerBandMedium;
	private double powerBandHigh;

	//Consumption
	private double startingEngineRunHours;
	private double finishEngineRunHours;
	private double fuelUsedLitres;//Fuel_Usedl
	private double fuelUsedIdleLitres;//Fuel_Used_In_Idle
	private String finishFuelLevel;
	private double overallFuelConsumptionLitres;
	
	private double startingEngineRunHoursLife;
	private double finishEngineRunHoursLife;
	private double fuelUsedLitresLife;
	private double fuelUsedIdleLitresLife;
	private String finishFuelLevelLife;
	
	//Returning parameters required for Report Grouping
	private String serialNumber;
	private String asset_group_name;
	private int assetGroupId;
	private String customMachineGroupName;
	private int customMachineGroupId;
	private String tenancyName;
	private int tenancyId;
	private String modelName;
	private int modelId;
	//DefectID:20150223 @Suprava Added DealerName as additional Parameter
	private String dealerName;

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
	public double getEngineOff() {
		return engineOff;
	}
	public void setEngineOff(double engineOff) {
		this.engineOff = engineOff;
	}
	public double getEngineOn() {
		return engineOn;
	}
	public void setEngineOn(double engineOn) {
		this.engineOn = engineOn;
	}
	public double getWorkingTime() {
		return workingTime;
	}
	public void setWorkingTime(double workingTime) {
		this.workingTime = workingTime;
	}
	public double getIdleTime() {
		return idleTime;
	}
	public void setIdleTime(double idleTime) {
		this.idleTime = idleTime;
	}
	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}
	public double getPowerBandLow() {
		return powerBandLow;
	}
	public void setPowerBandLow(double powerBandLow) {
		this.powerBandLow = powerBandLow;
	}
	public double getPowerBandMedium() {
		return powerBandMedium;
	}
	public void setPowerBandMedium(double powerBandMedium) {
		this.powerBandMedium = powerBandMedium;
	}
	public double getPowerBandHigh() {
		return powerBandHigh;
	}
	public void setPowerBandHigh(double powerBandHigh) {
		this.powerBandHigh = powerBandHigh;
	}

	public String getFinishFuelLevel() {
		return finishFuelLevel;
	}
	public void setFinishFuelLevel(String finishFuelLevel) {
		this.finishFuelLevel = finishFuelLevel;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getAsset_group_name() {
		return asset_group_name;
	}
	public void setAsset_group_name(String asset_group_name) {
		this.asset_group_name = asset_group_name;
	}


	public double getFuelUsedLitresLife() {
		return fuelUsedLitresLife;
	}
	public void setFuelUsedLitresLife(double fuelUsedLitresLife) {
		this.fuelUsedLitresLife = fuelUsedLitresLife;
	}
	public double getFuelUsedIdleLitresLife() {
		return fuelUsedIdleLitresLife;
	}
	public void setFuelUsedIdleLitresLife(double fuelUsedIdleLitresLife) {
		this.fuelUsedIdleLitresLife = fuelUsedIdleLitresLife;
	}
	public double getFinishEngineRunHours() {
		return finishEngineRunHours;
	}
	public void setFinishEngineRunHours(double finishEngineRunHours) {
		this.finishEngineRunHours = finishEngineRunHours;
	}
	
	public double getStartingEngineRunHours() {
		return startingEngineRunHours;
	}
	public void setStartingEngineRunHours(double startingEngineRunHours) {
		this.startingEngineRunHours = startingEngineRunHours;
	}
	public double getStartingEngineRunHoursLife() {
		return startingEngineRunHoursLife;
	}
	public void setStartingEngineRunHoursLife(double startingEngineRunHoursLife) {
		this.startingEngineRunHoursLife = startingEngineRunHoursLife;
	}
	public double getFinishEngineRunHoursLife() {
		return finishEngineRunHoursLife;
	}
	public void setFinishEngineRunHoursLife(double finishEngineRunHoursLife) {
		this.finishEngineRunHoursLife = finishEngineRunHoursLife;
	}
	public double getFuelUsedLitres() {
		return fuelUsedLitres;
	}
	public void setFuelUsedLitres(double fuelUsedLitres) {
		this.fuelUsedLitres = fuelUsedLitres;
	}
	public double getFuelUsedIdleLitres() {
		return fuelUsedIdleLitres;
	}
	public void setFuelUsedIdleLitres(double fuelUsedIdleLitres) {
		this.fuelUsedIdleLitres = fuelUsedIdleLitres;
	}
	public double getOverallFuelConsumptionLitres() {
		return overallFuelConsumptionLitres;
	}
	public void setOverallFuelConsumptionLitres(double overallFuelConsumptionLitres) {
		this.overallFuelConsumptionLitres = overallFuelConsumptionLitres;
	}
	public String getFinishFuelLevelLife() {
		return finishFuelLevelLife;
	}
	public void setFinishFuelLevelLife(String finishFuelLevelLife) {
		this.finishFuelLevelLife = finishFuelLevelLife;
	}
	public int getAssetGroupId() {
		return assetGroupId;
	}
	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}
	public String getCustomMachineGroupName() {
		return customMachineGroupName;
	}
	public void setCustomMachineGroupName(String customMachineGroupName) {
		this.customMachineGroupName = customMachineGroupName;
	}
	public int getCustomMachineGroupId() {
		return customMachineGroupId;
	}
	public void setCustomMachineGroupId(int customMachineGroupId) {
		this.customMachineGroupId = customMachineGroupId;
	}
	public String getTenancyName() {
		return tenancyName;
	}
	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}
	public int getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	
}
