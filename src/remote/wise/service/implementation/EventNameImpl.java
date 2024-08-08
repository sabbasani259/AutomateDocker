/**
 * 
 */
package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.EventNameReqContract;
import remote.wise.service.datacontract.EventNameRespContract;
import remote.wise.service.datacontract.EventTypeRespContract;

/**
 * @author roopn5
 *
 */
public class EventNameImpl {
	private int eventTypeId;
	
	private int eventId;
	private String eventName;
	


	/**
	 * @return the eventTypeId
	 */
	public int getEventTypeId() {
		return eventTypeId;
	}




	/**
	 * @param eventTypeId the eventTypeId to set
	 */
	public void setEventTypeId(int eventTypeId) {
		this.eventTypeId = eventTypeId;
	}






	/**
	 * @return the eventId
	 */
	public int getEventId() {
		return eventId;
	}




	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}




	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}




	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}




	/**
	 * method to get user alert preference
	 * @param alertReq
	 * @return List<UserAlertPreferenceRespContract>
	 * @throws CustomFault
	 */
	public List<EventNameRespContract> getEventNames(EventNameReqContract reqObj)throws CustomFault
	{
		
		
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		if(reqObj.getEventTypeId()!=null){
		for(int i=0;i<reqObj.getEventTypeId().size();i++){
			if(reqObj.getEventTypeId().get(i)==0){
				bLogger.error("EventTypeId is invalid");
				throw new CustomFault("EventTypeId is invalid");
			}
			
		}
		}
		
		
		
		List<EventNameRespContract> respList = new ArrayList<EventNameRespContract>();
		UserDetailsBO userBO=new UserDetailsBO();
		List<EventNameImpl> eventImplList = userBO.getEventNames(reqObj.getEventTypeId());	
		
		EventNameRespContract respObj = null;
		EventNameImpl implObj = null;
		Iterator<EventNameImpl> listIterator=eventImplList.iterator();
		while(listIterator.hasNext()){			
			implObj = listIterator.next();
			respObj = new EventNameRespContract();
			respObj.setEventTypeId(implObj.getEventTypeId());
			respObj.setEventId(implObj.getEventId());
			respObj.setEventName(implObj.getEventName());
			
			respList.add(respObj);
		}		
		return respList;		
	}

}
