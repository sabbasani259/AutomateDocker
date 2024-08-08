package remote.wise.service.implementation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

//import com.sun.swing.internal.plaf.basic.resources.basic;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.NotificationReportDetailsReqContract;
import remote.wise.service.datacontract.NotificationReportDetailsRespContract;
//import remote.wise.util.WiseLogger;


/**
 * 
 *  This Service gets the NotificationReportDetails
 *
 */
public class NotificationReportDetailsImpl {
	//Defect Id 1337 - Logger changes
	//public static WiseLogger businessError = WiseLogger.getLogger("NotificationReportDetailsImpl:","businessError");
	private String category;

	private String alertName;
	private String description;
	private String status;
	private String severity;
	private String dateRaised;
	private String location;
	private String machineName;
	private String serialNumber;
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
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getDescription() {
		return description;
	}
	public String getAlertName() {
		return alertName;
	}
	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}	
	
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getDateRaised() {
		return dateRaised;
	}
	public void setDateRaised(String dateRaised) {
		this.dateRaised = dateRaised;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
		
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	
	/**
	 * 
	 * @param reqObj provides the input to get summary on notification
	 * @return listResponse list of notification based on the asset
	 */
	
public List<NotificationReportDetailsRespContract> getNotificationReportDetails(NotificationReportDetailsReqContract reqObj)
{
	Logger bLogger = BusinessErrorLoggerClass.logger;
try
{

	
	
	//Custom Dates (fromDate,toDate) added by Juhi on 14-August-2013 
	//added by smitha...Defect ID-20131024 on oct 24th 2013
	if(((reqObj.getFromDate()==null) &&(reqObj.getToDate()==null)))
	{
		
		bLogger.error("Please pass either Period or custom dates");
		throw new CustomFault("Either Period or custom dates are not specified");
	}
	//ended on oct 24th 2013....DefectID-20131024
}

catch(CustomFault e)
{
	bLogger.error("Custom Fault: "+ e.getFaultInfo());
}
	
	NotificationReportDetailsRespContract response=null;
	List<NotificationReportDetailsRespContract> listResponse=new LinkedList<NotificationReportDetailsRespContract>();

	ReportDetailsBO ObjBO=new ReportDetailsBO();
	//Custom Dates (fromDate,toDate) added by Juhi on 14-August-2013 
	//added by smitha...Defect ID-20131024 on oct 24th 2013
	List<NotificationReportDetailsImpl> implResponse=ObjBO.getNotificationReportDetails(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getSerialNumberList(),reqObj.getTenancyIdList(),reqObj.getLoginTenancyIdList(),reqObj.getAlertTypeIdList(),reqObj.getMachineGroupIdList(),reqObj.getModelidList(),reqObj.getMachineProfileIdList());
	for(int i=0;i<implResponse.size();i++)
	{
		response=new NotificationReportDetailsRespContract();
		String cat=implResponse.get(i).getCategory();
    	response.setCategory(cat);
    	response.setAlertName(implResponse.get(i).getAlertName());
		response.setDescription(implResponse.get(i).getDescription());
		response.setStatus(implResponse.get(i).getStatus());		
		response.setSeverity(implResponse.get(i).getSeverity());
		response.setDateRaised(implResponse.get(i).getDateRaised());
		response.setLocation(implResponse.get(i).getLocation());
		response.setMachineName(implResponse.get(i).getMachineName());
		response.setSerialNumber(implResponse.get(i).getSerialNumber());
		response.setDealerName(implResponse.get(i).getDealerName());
		listResponse.add(response);
		
	}
	//ended on oct 24th 2013....DefectID-20131024
	return listResponse;
	
}	
}
