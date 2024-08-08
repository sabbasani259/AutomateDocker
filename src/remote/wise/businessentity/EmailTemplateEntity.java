package remote.wise.businessentity;

import java.io.Serializable;

public class EmailTemplateEntity extends BaseBusinessEntity implements Serializable
{
	EventEntity eventId;
	String emailSubject;
	String emailBody;
	
	public EventEntity getEventId() {
		return eventId;
	}
	public void setEventId(EventEntity eventId) {
		this.eventId = eventId;
	}
	public String getEmailSubject() {
		return emailSubject;
	}
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	public String getEmailBody() {
		return emailBody;
	}
	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}
	
	
}
