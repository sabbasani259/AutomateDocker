package remote.wise.service.datacontract;

import java.sql.Timestamp;
import java.util.List;

public class MachineHoursServiceReportRespContract {
	//DF:2013:12:13 : Conversion from Long to Double for all the required fields : Suprava
	private int tenancyId;
	private String TenancyName;
	private int MachineGroupId;
	private String MachineGroupName; 
	private int MachineProfileId;// AssetGroupId, TABLE-asset_class_dimension
	private String MachineProfileName;//AssetGroupName,TABLE-asset_class_dimension 
	private int  ModelId;// AssetTypeId,TABLE-asset_class_dimension
	private String ModelName;// AssetTypeName,TABLE-asset_class_dimension
	private String SerialNumber;
	private String NickName;
	private double TotalMachineLifeHours;
	private String severity;//added by smitha
	private String DealerName;//Added by Suprava 20150217
	//private int serviceScheduleId;//LastScheduledServicedHours;	 
//	private int durationSchedule;//LastScheduledServicedHours
	private String ScheduleName;//Added by Juhi;
	/**
	 * @return the dealerName
	 */
	public String getDealerName() {
		return DealerName;
	}
	/**
	 * @param dealerName the dealerName to set
	 */
	public void setDealerName(String dealerName) {
		DealerName = dealerName;
	}
	public String getScheduleName() {
		return ScheduleName;
	}
	public void setScheduleName(String scheduleName) {
		ScheduleName = scheduleName;
	}
	public String getServiceName() {
		return ServiceName;
	}
	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}
	private double LastServiceHour;//Added by Juhi;
	public double getLastServiceHour() {
		return LastServiceHour;
	}
	public void setLastServiceHour(double lastServiceHour) {
		LastServiceHour = lastServiceHour;
	}
	private String ApproximateServiceDate;//Added by Juhi;
	public String getApproximateServiceDate() {
		return ApproximateServiceDate;
	}
	public void setApproximateServiceDate(String approximateServiceDate) {
		ApproximateServiceDate = approximateServiceDate;
	}
	private double hoursToNextService;//Added by Juhi;
	public double getHoursToNextService() {
		return hoursToNextService;
	}
	public void setHoursToNextService(double hoursToNextService) {
		this.hoursToNextService = hoursToNextService;
	}
//	private int durationSchedule1;//NextPlannedServiceHours   
	
	private String ServiceName;//Added by Juhi;
	private String serviceDate;//LastActualServicedDate;
	//private String scheduleDate;//NextPlannedServiceDate;
	private String Location;
	private String Status;
//	private String scheduledDateList;//NextPlannedServiceDate;
	
	public int getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	public String getTenancyName() {
		return TenancyName;
	}
	public void setTenancyName(String tenancyName) {
		TenancyName = tenancyName;
	}
	public int getMachineGroupId() {
		return MachineGroupId;
	}
	public void setMachineGroupId(int machineGroupId) {
		MachineGroupId = machineGroupId;
	}
	public String getMachineGroupName() {
		return MachineGroupName;
	}
	public void setMachineGroupName(String machineGroupName) {
		MachineGroupName = machineGroupName;
	}
	public int getMachineProfileId() {
		return MachineProfileId;
	}
	public void setMachineProfileId(int machineProfileId) {
		MachineProfileId = machineProfileId;
	}
	public String getMachineProfileName() {
		return MachineProfileName;
	}
	public void setMachineProfileName(String machineProfileName) {
		MachineProfileName = machineProfileName;
	}
	public int getModelId() {
		return ModelId;
	}
	public void setModelId(int modelId) {
		ModelId = modelId;
	}
	public String getModelName() {
		return ModelName;
	}
	public void setModelName(String modelName) {
		ModelName = modelName;
	}
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	public String getNickName() {
		return NickName;
	}
	public void setNickName(String nickName) {
		NickName = nickName;
	}
	public double getTotalMachineLifeHours() {
		return TotalMachineLifeHours;
	}
	public void setTotalMachineLifeHours(double totalMachineLifeHours) {
		TotalMachineLifeHours = totalMachineLifeHours;
	}
	

	public String getServiceDate() {
		return serviceDate;
	}
	public void setServiceDate(String serviceDate) {
		this.serviceDate = serviceDate;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	
	/*public int getDurationSchedule() {
		return durationSchedule;
	}
	public void setDurationSchedule(int durationSchedule) {
		this.durationSchedule = durationSchedule;
	}
	public int getDurationSchedule1() {
		return durationSchedule1;
	}
	public void setDurationSchedule1(int durationSchedule1) {
		this.durationSchedule1 = durationSchedule1;
	}*/
/*	public String getScheduledDateList() {
		return scheduledDateList;
	}
	public void setScheduledDateList(String scheduledDateList) {
		this.scheduledDateList = scheduledDateList;
	}*/
	/*public String getLastService() {
		return lastService;
	}
	public void setLastService(String lastService) {
		this.lastService = lastService;
	}*/
	public String getNextService() {
		return nextService;
	}
	public void setNextService(String nextService) {
		this.nextService = nextService;
	}
	/*public String getServiceSchedule() {
		return serviceSchedule;
	}
	public void setServiceSchedule(String serviceSchedule) {
		this.serviceSchedule = serviceSchedule;
	}*/
//	private String lastService;//ServiceName,TABLE-Service_Schedule
    private String nextService;//serviceName, TABLE-asset_Service_Schedule
//    private String serviceSchedule;//serviceScheduleName ,TABLE-product_profile
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	
	
	
}
