package remote.wise.service.datacontract;

public class UserAlertPreferenceReqContract {

	
	private String loginId;
	private int EventTypeId;
	private String roleName;
	private int pageNumber;
	
	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/*private boolean isSMS;
	private boolean isEmail;
*/
	public int getEventTypeId() {
		return EventTypeId;
	}

	public void setEventTypeId(int eventTypeId) {
		EventTypeId = eventTypeId;
	}

	/*public boolean isSMS() {
		return isSMS;
	}

	public void setSMS(boolean isSMS) {
		this.isSMS = isSMS;
	}

	public boolean isEmail() {
		return isEmail;
	}

	public void setEmail(boolean isEmail) {
		this.isEmail = isEmail;
	}*/

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	
}
