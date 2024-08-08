package remote.wise.businessentity;

import java.io.Serializable;

public class SmsTemplateEntity extends BaseBusinessEntity implements Serializable
{
	private EventEntity eventId;
	private String smsBody;
	//DefectID:  - 1288 - Rajani Nagaraju - Custom SMS implementation
	private String templateId;
	
	public EventEntity getEventId() {
		return eventId;
	}
	public void setEventId(EventEntity eventId) {
		this.eventId = eventId;
	}
	public String getSmsBody() {
		return smsBody;
	}
	public void setSmsBody(String smsBody) {
		this.smsBody = smsBody;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	
}
