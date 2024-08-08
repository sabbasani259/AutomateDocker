package remote.wise.businessentity;

import java.sql.Timestamp;

public class SmsPendingEntity extends BaseBusinessEntity {

	private int reqId;
	private String mobileNumber;
	private String smsBody;
	private Timestamp generatedTime;
	
	public int getReqId() {
		return reqId;
	}
	public void setReqId(int reqId) {
		this.reqId = reqId;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getSmsBody() {
		return smsBody;
	}
	public void setSmsBody(String smsBody) {
		this.smsBody = smsBody;
	}
	public Timestamp getGeneratedTime() {
		return generatedTime;
	}
	public void setGeneratedTime(Timestamp generatedTime) {
		this.generatedTime = generatedTime;
	}
	
}
