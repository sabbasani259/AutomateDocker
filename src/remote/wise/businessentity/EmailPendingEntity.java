package remote.wise.businessentity;

import java.sql.Timestamp;

public class EmailPendingEntity extends BaseBusinessEntity{
	
	private int reqId;
	private String emailId;
	private String emailSubject;
	private String emailBody;
	private Timestamp generatedTime;
	public int getReqId() {
		return reqId;
	}
	public void setReqId(int reqId) {
		this.reqId = reqId;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
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
	public Timestamp getGeneratedTime() {
		return generatedTime;
	}
	public void setGeneratedTime(Timestamp generatedTime) {
		this.generatedTime = generatedTime;
	}
	
}
