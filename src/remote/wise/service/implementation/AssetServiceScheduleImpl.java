package remote.wise.service.implementation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.ServiceScheduleNewBO2;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetServiceScheduleGetReqContract;
import remote.wise.service.datacontract.AssetServiceScheduleGetRespContract;

public class AssetServiceScheduleImpl {
	
	private String dealerName;
	private String serviceName;
	private String scheduleName;
	private Long engineHoursSchedule;
	
	private Long hoursToNextService;
	private String serialNumber;
	private int dealerId;
	private int serviceScheduleId;
	private String scheduledDate;
	 //added by smitha on Aug 13th 2013
	private int eventId;
	//end [Aug 13th 2013]		
	//ramu b added on 20200512  extendedWarrantyType
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
	public Long getHoursToNextService() {
		return hoursToNextService;
	}
	public void setHoursToNextService(Long hoursToNextService) {
		this.hoursToNextService = hoursToNextService;
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
	
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	
	public List<AssetServiceScheduleGetRespContract> getAssetserviceSchedule(AssetServiceScheduleGetReqContract reqObj)throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
		List<AssetServiceScheduleGetRespContract> assetResponse=new LinkedList<AssetServiceScheduleGetRespContract>();
		AssetServiceScheduleGetRespContract respObj=null;
//		ServiceDetailsBO2 serviceBO=new ServiceDetailsBO2();		
		//List<AssetServiceScheduleImpl> implResponse= new LinkedList<AssetServiceScheduleImpl>();
		//List<AssetServiceScheduleImpl> implResponse=serviceBO.getAssetServiceScheduleBO(reqObj.getSerialNumber());
		
		//Invoking a new Class
		ServiceScheduleNewBO2 serviceBO = new ServiceScheduleNewBO2();
		List<AssetServiceScheduleImpl> implResponse=serviceBO.getServiceScheduleNew(reqObj.getSerialNumber());
		AssetServiceScheduleImpl implObj = null;
		Iterator<AssetServiceScheduleImpl> iterList = implResponse.iterator();
		while(iterList.hasNext()){
			
			respObj=new AssetServiceScheduleGetRespContract();
			implObj = (AssetServiceScheduleImpl)iterList.next();
			respObj.setDealerId(implObj.getDealerId());
			respObj.setScheduledDate(implObj.getScheduledDate());
			respObj.setSerialNumber(implObj.getSerialNumber());
			respObj.setServiceScheduleId(implObj.getServiceScheduleId());	
			respObj.setHoursToNextService(implObj.getHoursToNextService());		
			respObj.setDealerName(implObj.getDealerName());
			respObj.setEngineHoursSchedule(implObj.getEngineHoursSchedule());
			respObj.setScheduleName(implObj.getScheduleName());
			respObj.setServiceName(implObj.getServiceName());
			respObj.setEventId(implObj.getEventId());
			respObj.setExtendedWarrantyType(implObj.getExtendedWarrantyType());
			
			assetResponse.add(respObj);
		}
		iLogger.info("AssetServiceScheduleImpl... getAssetserviceSchedule....assetResponse "+assetResponse);
		return assetResponse;
	}
	
/*	public String setAssetserviceSchedule(AssetServiceScheduleRespContract respObj)throws CustomFault
	{
		ServiceDetailsBO serviceBO=new ServiceDetailsBO();
		String respImpl=serviceBO.setAssetServiceScheduleBO(respObj.getInstallationDate(),respObj.getSerialNumber());
		return respImpl;
	}
	*/
}
