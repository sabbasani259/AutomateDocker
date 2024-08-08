package remote.wise.service.datacontract;

//import java.sql.Timestamp;

import remote.wise.businessentity.AssetGroupEntity;

public class MasterServiceScheduleRespContract {
	//private int serviceScheduleId;
    private int assetGroupId;            
	private String serviceName;
    private String scheduleName;  
    private String serviceSchedule;
    private String UOM;
	
	
	private String serviceDate;
    private String dealerName;
	private long hoursToNextSvc;	
	private String jobCardNumber;
	private String SerialNumber;
	
	
	
	
	
	public String getServiceName() {
		return serviceName;
	}
	
	/*public int getServiceScheduleId() {
		return serviceScheduleId;
	}
	public void setServiceScheduleId(int serviceScheduleId) {
		this.serviceScheduleId = serviceScheduleId;
	}*/
	public int getAssetGroupId() {
		return assetGroupId;
	}
	public void setAssetGroupId(int assetGroupEntity) {
		this.assetGroupId = assetGroupEntity;
	}
	public String getServiceSchedule() {
		return serviceSchedule;
	}
	

	public void setServiceSchedule(String serviceSchedule) {
		this.serviceSchedule = serviceSchedule;
	}
	public String getUOM() {
		return UOM;
	}
	public void setUOM(String uOM) {
		UOM = uOM;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	
	public String getServiceDate() {
		return serviceDate;
	}
	public void setServiceDate(String serviceDate) {
		this.serviceDate = serviceDate;
	}
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public long getHoursToNextSvc() {
		return hoursToNextSvc;
	}
	public void setHoursToNextSvc(long hoursToNextSvc) {
		this.hoursToNextSvc = hoursToNextSvc;
	}
	public String getJobCardNumber() {
		return jobCardNumber;
	}
	public void setJobCardNumber(String jobCardNumber) {
		this.jobCardNumber = jobCardNumber;
	}
	
	
	
}
