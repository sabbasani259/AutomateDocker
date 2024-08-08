package remote.wise.service.datacontract;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.ServiceScheduleNewBO2;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetServiceScheduleImpl;

//CR373 - 20230125 - Prasad - Service History Api
public class AssetServiceScheduleGetRespContractApi {

	
	private String dealerName;
	private String serviceName;
	private String scheduleName;
	private Long engineHoursSchedule;
	
	private Long hoursToNextService;
//	private String serialNumber;
//	private int dealerId;
//	private int serviceScheduleId;
	private String scheduledDate;
	 //added by smitha on Aug 13th 2013
//	private int eventId;
	//end [Aug 13th 2013]		
	//ramu b added on 20200512  extendedWarrantyType
	private String currentCMH;
	private String status;
	public String getCurrentCMH() {
		return currentCMH;
	}

	public void setCurrentCMH(String currentCMH) {
		if (null == currentCMH)
			this.currentCMH = "NA";
		else
		this.currentCMH = currentCMH;
	}

	@Override
	public String toString() {
		return "AssetServiceScheduleGetRespContractApi [dealerName=" + dealerName + ", serviceName=" + serviceName
				+ ", scheduleName=" + scheduleName + ", engineHoursSchedule=" + engineHoursSchedule
				+ ", hoursToNextService=" + hoursToNextService + ", scheduledDate=" + scheduledDate + ", currentCMH="
				+ currentCMH + ", status=" + status + ", extendedWarrantyType=" + extendedWarrantyType + "]";
	}

	public String getStatus() {
		
		return status;
	}

	public void setStatus(String status) {
		if (null == status)
			this.status = "NA";
		else
		this.status = status;
	}

	private String extendedWarrantyType;

	public String getExtendedWarrantyType() {
		return extendedWarrantyType;
	}

	public void setExtendedWarrantyType(String extendedWarrantyType) {
		if (null == extendedWarrantyType)
			this.extendedWarrantyType = "NA";
		else
		this.extendedWarrantyType = extendedWarrantyType;
	}

	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		if (null == dealerName)
			this.dealerName = "NA";
		else
		this.dealerName = dealerName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		if (null == serviceName)
			this.serviceName = "NA";
		else
		this.serviceName = serviceName;
	}
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		if (null == scheduleName)
			this.scheduleName = "NA";
		else
		this.scheduleName = scheduleName;
	}
	public Long getEngineHoursSchedule() {
		return engineHoursSchedule;
	}
	public void setEngineHoursSchedule(Long engineHoursSchedule) {
		if (null == engineHoursSchedule)
			this.engineHoursSchedule = 0L;
		else
		this.engineHoursSchedule = engineHoursSchedule;
	}
	public Long getHoursToNextService() {
		return hoursToNextService;
	}
	public void setHoursToNextService(Long hoursToNextService) {
		if (null == hoursToNextService)
			this.hoursToNextService = 0L;
		else
		this.hoursToNextService = hoursToNextService;
	}
	
	public String getScheduledDate() {
		return scheduledDate;
	}
	public void setScheduledDate(String scheduledDate) {
		if (null == scheduledDate)
			this.scheduledDate = "NA";
		else
		this.scheduledDate = scheduledDate;
	}
	



}
