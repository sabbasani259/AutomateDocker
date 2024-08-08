package remote.wise.businessentity;

public class EventEntity extends BaseBusinessEntity
{
	int eventId;
	String eventName;
	EventTypeEntity eventTypeId;
	String eventDescription;
	int frequency;
	String UOM;
	boolean isSms;
	boolean isEmail;
	String eventSeverity;
	//DF20160413 - Rajani Nagaraju - Facilitate Communication Module - Setting UserAlertPref iin OrientDB
	String eventCode;
	
	int parameterID;

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}
	
	public int getParameterID() {
		return parameterID;
	}

	public void setParameterID(int parameterID) {
		this.parameterID = parameterID;
	}

	
	public EventEntity(){ }
	
	public EventEntity(int eventId)
	{
		super.key = new Integer(eventId);
		EventEntity e = (EventEntity)read(this);
		if(e!= null)
		{
			setEventId(e.getEventId());
			setEventName(e.getEventName());
			setEventTypeId(e.getEventTypeId());
			setEventDescription(e.getEventDescription());
			setFrequency(e.getFrequency());
			setUOM(e.getUOM());
			setSms(e.isSms());
			setEmail(e.isEmail());
			setEventSeverity(e.getEventSeverity());
			setEventCode(e.getEventCode());
			setParameterID(e.getParameterID());
		}
		
	}
	
	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	
	public EventTypeEntity getEventTypeId() {
		return eventTypeId;
	}
	public void setEventTypeId(EventTypeEntity eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public String getUOM() {
		return UOM;
	}

	public void setUOM(String uOM) {
		UOM = uOM;
	}

	public boolean isSms() {
		return isSms;
	}

	public void setSms(boolean isSms) {
		this.isSms = isSms;
	}

	public boolean isEmail() {
		return isEmail;
	}

	public void setEmail(boolean isEmail) {
		this.isEmail = isEmail;
	}

	public String getEventSeverity() {
		return eventSeverity;
	}

	public void setEventSeverity(String eventSeverity) {
		this.eventSeverity = eventSeverity;
	}
	
	
}
