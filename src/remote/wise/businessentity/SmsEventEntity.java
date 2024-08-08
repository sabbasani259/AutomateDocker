package remote.wise.businessentity;

import java.sql.Timestamp;

public class SmsEventEntity extends BaseBusinessEntity
{
	private int smsId;
	private SmsEventDetailsEntity smsEventId;
	private int eventId;
	private Timestamp eventClosedTime;
	private String status;
	
	/**
	 * @return the smsId
	 */
	public int getSmsId() {
		return smsId;
	}
	/**
	 * @param smsId the smsId to set
	 */
	public void setSmsId(int smsId) {
		this.smsId = smsId;
	}
	/**
	 * @return the smsEventId
	 */
	public SmsEventDetailsEntity getSmsEventId() {
		return smsEventId;
	}
	/**
	 * @param smsEventId the smsEventId to set
	 */
	public void setSmsEventId(SmsEventDetailsEntity smsEventId) {
		this.smsEventId = smsEventId;
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
	 * @return the eventClosedTime
	 */
	public Timestamp getEventClosedTime() {
		return eventClosedTime;
	}
	/**
	 * @param eventClosedTime the eventClosedTime to set
	 */
	public void setEventClosedTime(Timestamp eventClosedTime) {
		this.eventClosedTime = eventClosedTime;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
