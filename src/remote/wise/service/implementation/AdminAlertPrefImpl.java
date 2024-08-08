package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.EventTypeEntity;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AdminAlertPrefReqContract;
import remote.wise.service.datacontract.AdminAlertPrefRespContract;
//import remote.wise.util.WiseLogger;


/** Implementation class to set and get the mode of notification selected for each Alerts
 * @author Rajani Nagaraju
 *
 */
public class AdminAlertPrefImpl 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("AdminAlertPrefImpl:","businessError");
	
	int eventTypeId;
	String eventTypeName;
	String eventName;
	boolean isSMS;
	boolean isEmail;
	
	
	public int getEventTypeId() {
		return eventTypeId;
	}
	public void setEventTypeId(int eventTypeId) {
		this.eventTypeId = eventTypeId;
	}
	public String getEventTypeName() {
		return eventTypeName;
	}
	public void setEventTypeName(String eventTypeName) {
		this.eventTypeName = eventTypeName;
	}
	
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public boolean isSMS() {
		return isSMS;
	}
	public void setSMS(boolean isSMS) {
		this.isSMS = isSMS;
	}
	public boolean isEmail() {
		return isEmail;
	}
	public void setEmail(boolean isEmail) {
		this.isEmail = isEmail;
	}
	
	
	//********************************************** get Notification Mode for all events *****************************************
	
	/** This method returns the notification mode that has been set for each Alert
	 * @param reqObj Notification mode for a specific alert/alert type can be retrieved by specifying the required alert/alert type through this reqObj
	 * @return notification modes for all/specific alerts are returned
	 * @throws CustomFault custom exception is thrown when the user login id is not specified or invalid, event type id is invalid when specified
	 */
	public List<AdminAlertPrefRespContract> getEventMode(AdminAlertPrefReqContract reqObj) throws CustomFault
	{
		List<AdminAlertPrefRespContract> responseList = new LinkedList<AdminAlertPrefRespContract>();
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	
		 // Logger businessError = Logger.getLogger("businessErrorLogger");
		  		
			if(reqObj.getLoginId()==null)
			{
				bLogger.error("User LoginId not specified");
				throw new CustomFault("Provide Login Id");
			}
			
			
			//validate loginId
			DomainServiceImpl domainService = new DomainServiceImpl();
			ContactEntity contact = domainService.getContactDetails(reqObj.getLoginId());
			if(contact==null || contact.getContact_id()==null)
			{
				bLogger.error("Invalid LoginId");
				throw new CustomFault("Invalid LoginId");
			}
			
			//validate EventTypeId if specified
			if(reqObj.getEventTypeId()!=0)
			{
				EventTypeEntity eventType = domainService.getEventTypeDetails(reqObj.getEventTypeId());
				if(eventType==null||eventType.getEventTypeId()==0)
				{
					bLogger.error("Alert Type Id specified is Invalid");
					throw new CustomFault("Invalid Event Type Id");
				}
			}
			
			EventDetailsBO eventDetails = new EventDetailsBO();
			List<AdminAlertPrefImpl> adminAlertPrefList = eventDetails.getEventAlertMode(reqObj.getLoginId(),reqObj.getEventName(),reqObj.getEventTypeId());
			
			for(int i=0; i<adminAlertPrefList.size(); i++)
			{
				AdminAlertPrefRespContract response = new AdminAlertPrefRespContract();
				response.setEventName(adminAlertPrefList.get(i).getEventName());
				response.setEventTypeId(adminAlertPrefList.get(i).getEventTypeId());
				response.setEventTypeName(adminAlertPrefList.get(i).getEventTypeName());
				response.setSMS(adminAlertPrefList.get(i).isSMS());
				response.setEmail(adminAlertPrefList.get(i).isEmail());
				
				responseList.add(response);
			}
					
			return responseList;
		
	}
	//**********************************************END of get Notification Mode for all events *****************************************
	
	
	
	//************************************************* set notification Mode for all events ************************************
	
	/** Set the Notification mode for each Alert
	 * @param reqObjList List of alerts with their notification modes to be set
	 * @return status String is returned as SUCCESS/FAILURE
	 * @throws CustomFault
	 */
	public String setEventMode(List<AdminAlertPrefRespContract> reqObjList) throws CustomFault
	{
		EventDetailsBO eventDetails = new EventDetailsBO();
		String status = eventDetails.setEventAlertMode(reqObjList);
		
		return status;
	}
	//*************************************************END of set notification Mode for all events ************************************
	
	
}
