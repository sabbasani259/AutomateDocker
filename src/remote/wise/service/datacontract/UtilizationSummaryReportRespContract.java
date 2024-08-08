package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class UtilizationSummaryReportRespContract 
{
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	int tenancyId;
	String tenancyName;
	int machineGroupId;
	String machineGroupName;
	int machineProfileId;
	String machineProfileName;
	int modelId;
	String modelName;
	
	String machineName;
	String serialNumber;
	double engineWorkingDurationInMin;
	Double engineRunDurationInMin;
	double engineOffDurationInMin;
	Double machineUtilizationPercentage;
	//DefectId:20150220 @ Suprava-- Dealer As a new parameter Added
	String dealerName;
	
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
	 * @return the tenancyId
	 */
	public int getTenancyId() {
		return tenancyId;
	}
	/**
	 * @param tenancyId the tenancyId to set as Integer input
	 */
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	
	
	/**
	 * @return the tenancyName
	 */
	public String getTenancyName() {
		return tenancyName;
	}
	/**
	 * @param tenancyName the tenancyName to set as String input
	 */
	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}
	
	
	/**
	 * @return the machineGroupId
	 */
	public int getMachineGroupId() {
		return machineGroupId;
	}
	/**
	 * @param machineGroupId the machineGroupId to set as Integer input
	 */
	public void setMachineGroupId(int machineGroupId) {
		this.machineGroupId = machineGroupId;
	}
	
	
	/**
	 * @return the machineGroupName
	 */
	public String getMachineGroupName() {
		return machineGroupName;
	}
	/**
	 * @param machineGroupName the machineGroupName to set as String input
	 */
	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}
	
	
	/**
	 * @return the machineProfileId
	 */
	public int getMachineProfileId() {
		return machineProfileId;
	}
	/**
	 * @param machineProfileId the machineProfileId to set as Integer input
	 */
	public void setMachineProfileId(int machineProfileId) {
		this.machineProfileId = machineProfileId;
	}
	
	
	/**
	 * @return the machineProfileName
	 */
	public String getMachineProfileName() {
		return machineProfileName;
	}
	/**
	 * @param machineProfileName the machineProfileName to set as String input
	 */
	public void setMachineProfileName(String machineProfileName) {
		this.machineProfileName = machineProfileName;
	}
	
	
	/**
	 * @return the modelId
	 */
	public int getModelId() {
		return modelId;
	}
	/**
	 * @param modelId the modelId to set as Integer input
	 */
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	
	
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}
	/**
	 * @param modelName the modelName to set  as String input
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	
	/**
	 * @return the machineName
	 */
	public String getMachineName() {
		return machineName;
	}
	/**
	 * @param machineName the machineName to set as String input
	 */
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	
	
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set as String input
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	/**
	 * @return the engineWorkingDurationInMin
	 */
	public double getEngineWorkingDurationInMin() {
		return engineWorkingDurationInMin;
	}
	/**
	 * @param engineWorkingDurationInMin the engineWorkingDurationInMin to set as Long
	 */
	public void setEngineWorkingDurationInMin(double engineWorkingDurationInMin) {
		this.engineWorkingDurationInMin = engineWorkingDurationInMin;
	}
	
	
	/**
	 * @return the engineRunDurationInMin
	 */
	public Double getEngineRunDurationInMin() {
		return engineRunDurationInMin;
	}
	/**
	 * @param engineRunDurationInMin the engineRunDurationInMin to set as Long
	 */
	public void setEngineRunDurationInMin(Double engineRunDurationInMin) {
		this.engineRunDurationInMin = engineRunDurationInMin;
	}
	
	
	/**
	 * @return the engineOffDurationInMin
	 */
	public double getEngineOffDurationInMin() {
		return engineOffDurationInMin;
	}
	/**
	 * @param engineOffDurationInMin the engineOffDurationInMin to set as Long
	 */
	public void setEngineOffDurationInMin(double engineOffDurationInMin) {
		this.engineOffDurationInMin = engineOffDurationInMin;
	}
	
	
	/**
	 * @return the machineUtilizationPercentage
	 */
	public Double getMachineUtilizationPercentage() {
		return machineUtilizationPercentage;
	}
	/**
	 * @param machineUtilizationPercentage the machineUtilizationPercentage to set as double
	 */
	public void setMachineUtilizationPercentage(Double machineUtilizationPercentage) {
		this.machineUtilizationPercentage = machineUtilizationPercentage;
	}
	
	
	
	
}
