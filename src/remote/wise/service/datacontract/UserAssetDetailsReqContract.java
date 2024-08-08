package remote.wise.service.datacontract;

public class UserAssetDetailsReqContract 
{
	String serialNumber;
	String loginId;
	int userTenancyId;
	String lifeHours;
	
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public int getUserTenancyId() {
		return userTenancyId;
	}
	public void setUserTenancyId(int userTenancyId) {
		this.userTenancyId = userTenancyId;
	}
	public String getLifeHours() {
		return lifeHours;
	}
	public void setLifeHours(String lifeHours) {
		this.lifeHours = lifeHours;
	}
	
	
}
