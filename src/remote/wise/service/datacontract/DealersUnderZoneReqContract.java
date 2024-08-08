/**
 * 
 */
package remote.wise.service.datacontract;

/**
 * @author kprabhu5
 *
 */
public class DealersUnderZoneReqContract {
	
	String loginId;
	int loginTenancyId;
	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	/**
	 * @return the loginTenancyId
	 */
	public int getLoginTenancyId() {
		return loginTenancyId;
	}
	/**
	 * @param loginTenancyId the loginTenancyId to set
	 */
	public void setLoginTenancyId(int loginTenancyId) {
		this.loginTenancyId = loginTenancyId;
	}
	
	

}
