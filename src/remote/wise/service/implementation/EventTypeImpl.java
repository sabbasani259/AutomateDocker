/**
 * 
 */
package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.EventTypeRespContract;
import remote.wise.service.datacontract.UserAlertPreferenceReqContract;
import remote.wise.service.datacontract.UserAlertPreferenceRespContract;

/**
 * @author roopn5
 *
 */
public class EventTypeImpl {
	private int eventTypeId;
	private String eventTypeName;
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
	 * @return the eventTypeName
	 */
	public String getEventTypeName() {
		return eventTypeName;
	}
	/**
	 * @param eventTypeName the eventTypeName to set
	 */
	public void setEventTypeName(String eventTypeName) {
		this.eventTypeName = eventTypeName;
	}
	
	

	/**
	 * method to get user alert preference
	 * @param alertReq
	 * @return List<UserAlertPreferenceRespContract>
	 * @throws CustomFault
	 */
	public List<EventTypeRespContract> getEventTypes()throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		
		
		List<EventTypeRespContract> respList = new ArrayList<EventTypeRespContract>();
		UserDetailsBO userBO=new UserDetailsBO();
		List<EventTypeImpl> eventTypeImplList = userBO.getEventTypes();	
		
		EventTypeRespContract respObj = null;
		EventTypeImpl implObj = null;
		Iterator<EventTypeImpl> listIterator=eventTypeImplList.iterator();
		while(listIterator.hasNext()){			
			implObj = listIterator.next();
			respObj = new EventTypeRespContract();
			respObj.setEventTypeId(implObj.getEventTypeId());
			respObj.setEventTypeName(implObj.getEventTypeName());
			respList.add(respObj);
		}		
		return respList;		
	}

}
