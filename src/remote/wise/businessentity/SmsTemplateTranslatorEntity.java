package remote.wise.businessentity;

import java.io.Serializable;

public class SmsTemplateTranslatorEntity extends BaseBusinessEntity implements Serializable{
	int seqId;
	String smsBody;
	int eventId;
	String unicode;
	String templateId;
	/**
	 * @return the seqId
	 */
	public int getSeqId() {
		return seqId;
	}
	/**
	 * @param seqId the seqId to set
	 */
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	/**
	 * @return the smsBody
	 */
	public String getSmsBody() {
		return smsBody;
	}
	/**
	 * @param smsBody the smsBody to set
	 */
	public void setSmsBody(String smsBody) {
		this.smsBody = smsBody;
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
	 * @return the unicode
	 */
	public String getUnicode() {
		return unicode;
	}
	/**
	 * @param unicode the unicode to set
	 */
	public void setUnicode(String unicode) {
		this.unicode = unicode;
	}
	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}
	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	

}
