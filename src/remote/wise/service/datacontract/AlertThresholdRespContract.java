package remote.wise.service.datacontract;

import java.util.LinkedList;
import java.util.List;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.implementation.DomainServiceImpl;
//import remote.wise.service.implementation.UserAlertPreferenceImpl;

public class AlertThresholdRespContract {
	private int EventTypeId;
	private String eventTypeName;
	private int EventId;
	private boolean isYellowThreshold;
	private boolean isRedThreshold;
	private int YellowThresholdVal;
	private int RedThresholdVal;
	private String EventName;

	
	public String getEventName() {
		return EventName;
	}
	public void setEventName(String eventName) {
		EventName = eventName;
	}
	public int getEventTypeId() {
		return EventTypeId;
	}
	public void setEventTypeId(int eventTypeId) {
		EventTypeId = eventTypeId;
	}
	public int getEventId() {
		return EventId;
	}
	public void setEventId(int eventId) {
		EventId = eventId;
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
	public String getEventTypeName() {
		return eventTypeName;
	}
	public void setEventTypeName(String eventTypeName) {
		this.eventTypeName = eventTypeName;
	}
	
	
	
	
}
