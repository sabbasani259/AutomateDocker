/*DF-100000266: 20220307: DH20313904: Alert Preference Update in table MUserAlertPref. 
 *Event type Id(Integer) getting updated in table instead of event type code(String).
 *Changes added to update table with correct data type.
 *CR500 : 20241128 : Dhiraj Kumar : WHatsApp Integration with LL
 */

package remote.wise.service.datacontract;

public class UserAlertPreferenceRespContract {

	private int eventId;
	private String eventName;
	private String roleName;
	private int EventTypeId;
	private boolean SMSEvent;
	private boolean EmailEvent;
	private String EventTypeName;
	private String loginId;
	//CR500.sn
	private boolean whatsappEvent;

	public boolean isWhatsappEvent() {
		return whatsappEvent;
	}

	public void setWhatsappEvent(boolean whatsappEvent) {
		this.whatsappEvent = whatsappEvent;
	}
	//CR500.en
	//DF-100000266.sn 
	private String eventTypeCode;

	public String getEventTypeCode() {
		return eventTypeCode;
	}

	public void setEventTypeCode(String eventTypeCode) {
		this.eventTypeCode = eventTypeCode;
	}
	//DF-100000266.en

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getEventTypeName() {
		return EventTypeName;
	}

	public void setEventTypeName(String eventTypeName) {
		EventTypeName = eventTypeName;
	}

	public int getEventTypeId() {
		return EventTypeId;
	}

	public void setEventTypeId(int eventTypeId) {
		EventTypeId = eventTypeId;
	}

	public boolean isSMSEvent() {
		return SMSEvent;
	}

	public void setSMSEvent(boolean sMSEvent) {
		SMSEvent = sMSEvent;
	}

	public boolean isEmailEvent() {
		return EmailEvent;
	}

	public void setEmailEvent(boolean emailEvent) {
		EmailEvent = emailEvent;
	}
}
