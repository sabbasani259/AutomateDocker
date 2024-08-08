package remote.wise.businessentity;

public class EventTypeEntity extends BaseBusinessEntity
{
	int eventTypeId;
	String eventTypeName;
	
	//DF20160413 - Rajani Nagaraju - Facilitate Communication Module - Setting UserAlertPref iin OrientDB
	String eventTypeCode;
	
	public String getEventTypeCode() {
		return eventTypeCode;
	}

	public void setEventTypeCode(String eventTypeCode) {
		this.eventTypeCode = eventTypeCode;
	}
	

	public EventTypeEntity() {}
	
	public EventTypeEntity(int eventTypeId)
	{
		super.key = new Integer(eventTypeId);
		EventTypeEntity e = (EventTypeEntity) read(this);
		if(e != null)
		{
			setEventTypeId(e.getEventTypeId());
			setEventTypeName(e.getEventTypeName());
		}
	}
	
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
	
}
