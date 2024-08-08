package remote.wise.service.datacontract;

import java.sql.Timestamp;
import java.util.List;

import remote.wise.businessentity.MachineGroupDimensionEntity;


public class MachineHoursReportRespContract {
	
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
    private String machineGroupName;//TABLE-machine_group_dimension
    private int machineGroupId;
    private int tenancyId;
    
  //DefectID:1383 - Rajani Nagaraju - 20130930 - Query modification to return correct resultset
    private int modelId;
    private String modelName;
    
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
	 * @param totalMachineHours the totalMachineHours to set
	 */
	public void setTotalMachineHours(int totalMachineHours) {
		TotalMachineHours = totalMachineHours;
	}
	private String machineName;
	private int machineProfileId;
	private double TotalMachineHours;
	private double MachineHours;//InPeriod;
	private String status;
	private String lastEngineRun;
	private String lastReported;
	private String location;
	private String machineProfileName;
	private String tenancyName;
	private String assetGroupName;
	private long durationInCurrentStatus;
	private String serialNumber;
	//DefectId:20150220 @ Suprava Added New Parameter
	private String dealerName;
	
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getTenancyName() {
		return tenancyName;
	}
	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}
	public String getAssetGroupName() {
		return assetGroupName;
	}
	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}
	public long getDurationInCurrentStatus() {
		return durationInCurrentStatus;
	}
	public void setDurationInCurrentStatus(long durationInCurrentStatus) {
		this.durationInCurrentStatus = durationInCurrentStatus;
	}
	public String getMachineGroupName() {
		return machineGroupName;
	}
	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public int getMachineProfileId() {
		return machineProfileId;
	}
	public void setMachineProfileId(int machineProfileId) {
		this.machineProfileId = machineProfileId;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getLastEngineRun() {
		return lastEngineRun;
	}
	public void setLastEngineRun(String lastEngineRun) {
		this.lastEngineRun = lastEngineRun;
	}
	
	public double getTotalMachineHours() {
		return TotalMachineHours;
	}
	public void setTotalMachineHours(double totalMachineHours) {
		TotalMachineHours = totalMachineHours;
	}
	public double getMachineHours() {
		return MachineHours;
	}
	public void setMachineHours(double machineHours) {
		MachineHours = machineHours;
	}
	
	public String getLastReported() {
		return lastReported;
	}
	public void setLastReported(String lastReported) {
		this.lastReported = lastReported;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getMachineProfileName() {
		return machineProfileName;
	}
	public void setMachineProfileName(String machineProfileName) {
		this.machineProfileName = machineProfileName;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	

}
