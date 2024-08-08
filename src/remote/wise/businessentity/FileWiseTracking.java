package remote.wise.businessentity;

import java.util.HashMap;
//DF20180207 @Maniratnam :: Adding new table File wise tracking as part of Interfaces Backtracking : contains vin specific interface details
public class FileWiseTracking extends BaseBusinessEntity{

	private String serialNumber;
	private String rollOff;
	private String gateOut;
	private String saleD2C;
	private String personality;
	private String installation;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getRollOff() {
		return rollOff;
	}
	public void setRollOff(String rollOff) {
		this.rollOff = rollOff;
	}
	public String getGateOut() {
		return gateOut;
	}
	public void setGateOut(String gateOut) {
		this.gateOut = gateOut;
	}
	public String getSaleD2C() {
		return saleD2C;
	}
	public void setSaleD2C(String saleD2C) {
		this.saleD2C = saleD2C;
	}
	public String getPersonality() {
		return personality;
	}
	public void setPersonality(String personality) {
		this.personality = personality;
	}
	public String getInstallation() {
		return installation;
	}
	public void setInstallation(String installation) {
		this.installation = installation;
	}

	

	
}
