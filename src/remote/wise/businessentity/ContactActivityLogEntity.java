package remote.wise.businessentity;

import java.sql.Timestamp;

public class ContactActivityLogEntity extends BaseBusinessEntity 
{
	int contact_activitylog_id;
	ContactEntity contact_id;
	Timestamp login_date;
	Timestamp logout_date;
	
	public ContactActivityLogEntity(){}
	
	
	
	public int getContact_activitylog_id() {
		return contact_activitylog_id;
	}
	public void setContact_activitylog_id(int contact_activitylog_id) {
		this.contact_activitylog_id = contact_activitylog_id;
	}
	public ContactEntity getContact_id() {
		return contact_id;
	}
	public void setContact_id(ContactEntity contact_id) {
		this.contact_id = contact_id;
	}
	public Timestamp getLogin_date() {
		return login_date;
	}
	public void setLogin_date(Timestamp login_date) {
		this.login_date = login_date;
	}
	public Timestamp getLogout_date() {
		return logout_date;
	}
	public void setLogout_date(Timestamp logout_date) {
		this.logout_date = logout_date;
	}
	
}
