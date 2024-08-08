package remote.wise.service.datacontract;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.ServiceScheduleEntity;

public class AssetServiceScheduleGetRespContract {

	private String serialNumber;
	private int dealerId;
	private int serviceScheduleId;
	private String scheduledDate;
	 //added by smitha on june 27th 2013 Defect ID 690
	private Long hoursToNextService;
	//end [june 27th 2013]
	private String dealerName;
	private String serviceName;

	private String scheduleName;
	private Long engineHoursSchedule;
	 //added by smitha on Aug 13th 2013
	private int eventId;
	//end [Aug 13th 2013]
	
	//ramu b added on 20200512 extendedWarrantyType
	private String extendedWarrantyType;
	public String getExtendedWarrantyType() {
		return extendedWarrantyType;
	}
	public void setExtendedWarrantyType(String extendedWarrantyType) {
		this.extendedWarrantyType = extendedWarrantyType;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public String getServiceName() {
		return serviceName;
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
	public Long getEngineHoursSchedule() {
		return engineHoursSchedule;
	}
	public void setEngineHoursSchedule(Long engineHoursSchedule) {
		this.engineHoursSchedule = engineHoursSchedule;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public int getDealerId() {
		return dealerId;
	}
	public void setDealerId(int dealerId) {
		this.dealerId = dealerId;
	}
	public int getServiceScheduleId() {
		return serviceScheduleId;
	}
	public void setServiceScheduleId(int serviceScheduleId) {
		this.serviceScheduleId = serviceScheduleId;
	}
	public String getScheduledDate() {
		return scheduledDate;
	}
	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	 //added by smitha on june 27th 2013 Defect ID 690
	public Long getHoursToNextService() {
		return hoursToNextService;
	}
	public void setHoursToNextService(Long hoursToNextService) {
		this.hoursToNextService = hoursToNextService;
	}
	//end [june 27th 2013]
	 //added by smitha on Aug 13th 2013
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	//end [Aug 13th 2013]
	@Override
	public String toString() {
		return "AssetServiceScheduleGetRespContract [serialNumber="
				+ serialNumber + ", dealerId=" + dealerId
				+ ", serviceScheduleId=" + serviceScheduleId
				+ ", scheduledDate=" + scheduledDate + ", hoursToNextService="
				+ hoursToNextService + ", dealerName=" + dealerName
				+ ", serviceName=" + serviceName + ", scheduleName="
				+ scheduleName + ", engineHoursSchedule=" + engineHoursSchedule
				+ ", eventId=" + eventId + ", extendedWarrantyType="
				+ extendedWarrantyType + "]";
	}
}
