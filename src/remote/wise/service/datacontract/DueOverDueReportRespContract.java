package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class DueOverDueReportRespContract 
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
	
	String customerName;
	String customerContactNumber;
	String operatorName;
	String operatorContactNumber;
	String dealerName;
	String dealerContactNumber;
	
	double dueOrOverDueHours;
	int dueOrOverDueDays;
	
	String serviceName;
	String scheduleName;
	String plannedServiceDate;
	double plannedServiceHours;
	double totalMachineHours;
	String DealerName;
	
	public double getTotalMachineHours() {
		return totalMachineHours;
	}
	public void setTotalMachineHours(double totalMachineHours) {
		this.totalMachineHours = totalMachineHours;
	}
	/**
	 * @return the tenancyId
	 */
	public int getTenancyId() {
		return tenancyId;
	}
	/**
	 * @param tenancyId the tenancyId to set
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
	 * @param tenancyName the tenancyName to set
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
	 * @param machineGroupId the machineGroupId to set
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
	 * @param machineGroupName the machineGroupName to set
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
	 * @param machineProfileId the machineProfileId to set
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
	 * @param machineProfileName the machineProfileName to set
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
	 * @param modelId the modelId to set
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
	 * @param modelName the modelName to set
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
	 * @param machineName the machineName to set
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
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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
	 * @return the customerContactNumber
	 */
	public String getCustomerContactNumber() {
		return customerContactNumber;
	}
	/**
	 * @param customerContactNumber the customerContactNumber to set
	 */
	public void setCustomerContactNumber(String customerContactNumber) {
		this.customerContactNumber = customerContactNumber;
	}
	/**
	 * @return the operatorName
	 */
	public String getOperatorName() {
		return operatorName;
	}
	/**
	 * @param operatorName the operatorName to set
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	/**
	 * @return the operatorContactNumber
	 */
	public String getOperatorContactNumber() {
		return operatorContactNumber;
	}
	/**
	 * @param operatorContactNumber the operatorContactNumber to set
	 */
	public void setOperatorContactNumber(String operatorContactNumber) {
		this.operatorContactNumber = operatorContactNumber;
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
	 * @return the dealerContactNumber
	 */
	public String getDealerContactNumber() {
		return dealerContactNumber;
	}
	/**
	 * @param dealerContactNumber the dealerContactNumber to set
	 */
	public void setDealerContactNumber(String dealerContactNumber) {
		this.dealerContactNumber = dealerContactNumber;
	}
	/**
	 * @return the dueOrOverDueHours
	 */
	public double getDueOrOverDueHours() {
		return dueOrOverDueHours;
	}
	/**
	 * @param dueOrOverDueHours the dueOrOverDueHours to set
	 */
	public void setDueOrOverDueHours(double dueOrOverDueHours) {
		this.dueOrOverDueHours = dueOrOverDueHours;
	}
	/**
	 * @return the dueOrOverDueDays
	 */
	public int getDueOrOverDueDays() {
		return dueOrOverDueDays;
	}
	/**
	 * @param dueOrOverDueDays the dueOrOverDueDays to set
	 */
	public void setDueOrOverDueDays(int dueOrOverDueDays) {
		this.dueOrOverDueDays = dueOrOverDueDays;
	}
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}
	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	/**
	 * @return the scheduleName
	 */
	public String getScheduleName() {
		return scheduleName;
	}
	/**
	 * @param scheduleName the scheduleName to set
	 */
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	/**
	 * @return the plannedServiceDate
	 */
	public String getPlannedServiceDate() {
		return plannedServiceDate;
	}
	/**
	 * @param plannedServiceDate the plannedServiceDate to set
	 */
	public void setPlannedServiceDate(String plannedServiceDate) {
		this.plannedServiceDate = plannedServiceDate;
	}
	/**
	 * @return the plannedServiceHours
	 */
	public double getPlannedServiceHours() {
		return plannedServiceHours;
	}
	/**
	 * @param plannedServiceHours the plannedServiceHours to set
	 */
	public void setPlannedServiceHours(double plannedServiceHours) {
		this.plannedServiceHours = plannedServiceHours;
	}
	
	
}
