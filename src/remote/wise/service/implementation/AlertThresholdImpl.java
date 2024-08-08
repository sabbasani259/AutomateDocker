package remote.wise.service.implementation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
////import org.apache.log4j.Logger;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AlertThresholdReqContract;
import remote.wise.service.datacontract.AlertThresholdRespContract;
//import remote.wise.util.WiseLogger;

public class AlertThresholdImpl {

	private int EventTypeId;
	private String EventName;
	private int EventId;
	private String EventTypeName;
	private boolean isYellowThreshold;
	private boolean isRedThreshold;
	private int YellowThresholdVal;
	private int RedThresholdVal;
	
	//public static WiseLogger businessError = WiseLogger.getLogger("AlertThresholdImpl:","businessError");
	
	public String getEventTypeName() {
		return EventTypeName;
	}
	public void setEventTypeName(String eventTypeName) {
		EventTypeName = eventTypeName;
	}
	public boolean isYellowThreshold() {
		return isYellowThreshold;
	}
	public void setYellowThreshold(boolean isYellowThreshold) {
		this.isYellowThreshold = isYellowThreshold;
	}
	public boolean isRedThreshold() {
		return isRedThreshold;
	}
	public void setRedThreshold(boolean isRedThreshold) {
		this.isRedThreshold = isRedThreshold;
	}
	public int getYellowThresholdVal() {
		return YellowThresholdVal;
	}
	public void setYellowThresholdVal(int yellowThresholdVal) {
		YellowThresholdVal = yellowThresholdVal;
	}
	public int getRedThresholdVal() {
		return RedThresholdVal;
	}
	public void setRedThresholdVal(int redThresholdVal) {
		RedThresholdVal = redThresholdVal;
	}
	public int getEventTypeId() {
		return EventTypeId;
	}
	public void setEventTypeId(int eventTypeId) {
		EventTypeId = eventTypeId;
	}
	public String getEventName() {
		return EventName;
	}
	public void setEventName(String eventName) {
		EventName = eventName;
	}
	public int getEventId() {
		return EventId;
	}
	public void setEventId(int eventId) {
		EventId = eventId;
	}
	
	/**
	 * method to get alert threshold settings 
	 * @param alertReq
	 * @return List<AlertThresholdRespContract>
	 * @throws CustomFault
	 */
	public List<AlertThresholdRespContract> getAlertThresholdSettings(AlertThresholdReqContract alertReq)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	
		if(alertReq.getLoginId()==null){
			bLogger.error("Login Id is invalid");
			throw new CustomFault("Login Id is invalid");
		}
		List<AlertThresholdRespContract> respList = new LinkedList<AlertThresholdRespContract>();
		EventDetailsBO eventBO=new EventDetailsBO();
		List<AlertThresholdImpl> alertThresholdImplList = eventBO.getAlertThresholdSettings(alertReq.getLoginId());
		AlertThresholdRespContract respObj = null;
		Iterator<AlertThresholdImpl> iter=alertThresholdImplList.iterator();
		AlertThresholdImpl alertThresholdImpl = null;
		while(iter.hasNext()){			
			alertThresholdImpl = iter.next();
			respObj = new AlertThresholdRespContract();
			respObj.setEventId(alertThresholdImpl.getEventId());
			respObj.setEventName(alertThresholdImpl.getEventName());
			respObj.setEventTypeId(alertThresholdImpl.getEventTypeId());
			respObj.setEventTypeName(alertThresholdImpl.getEventTypeName());
			respObj.setRedThreshold(alertThresholdImpl.isRedThreshold());
			respObj.setYellowThreshold(alertThresholdImpl.isYellowThreshold());
			respObj.setRedThresholdVal(alertThresholdImpl.getRedThresholdVal());
			respObj.setYellowThresholdVal(alertThresholdImpl.getYellowThresholdVal());
			respList.add(respObj);
		}		
		return respList;		
	}
	
	/**
	 * method to set alert threshold settings
	 * @param respObj
	 * @return
	 * @throws CustomFault
	 */
	public String setAlertThresholdSettings(List<AlertThresholdRespContract> respObj) throws CustomFault{
			EventDetailsBO eventBO = new EventDetailsBO();
			String flag = eventBO.setAlertThresholdSettings(respObj);		
		return "Success";
	}
}
