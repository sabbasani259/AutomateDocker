package remote.wise.businessentity;

import java.io.Serializable;

public class AccountMapping extends BaseBusinessEntity implements Serializable
{
	private String eccAccCode;
	private String crmAccCode;
	private String llAccCode;
	private String eccAccName;
	
	
	public String getEccAccCode() {
		return eccAccCode;
	}
	public void setEccAccCode(String eccAccCode) {
		this.eccAccCode = eccAccCode;
	}
	public String getCrmAccCode() {
		return crmAccCode;
	}
	public void setCrmAccCode(String crmAccCode) {
		this.crmAccCode = crmAccCode;
	}
	public String getLlAccCode() {
		return llAccCode;
	}
	public void setLlAccCode(String llAccCode) {
		this.llAccCode = llAccCode;
	}
	public String getEccAccName() {
		return eccAccName;
	}
	public void setEccAccName(String eccAccName) {
		this.eccAccName = eccAccName;
	}
	
	
}
