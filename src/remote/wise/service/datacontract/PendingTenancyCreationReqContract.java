package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class PendingTenancyCreationReqContract 
{
	String loginId;
	List<Integer> tenancyIdList;
	
	public PendingTenancyCreationReqContract()
	{
		loginId = null;
		tenancyIdList =null;
	}
	
	/**
	 * @return userLoginID
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId userLoginId as String input
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	
	/**
	 * @return List of tenancy ID
	 */
	public List<Integer> getTenancyIdList() {
		return tenancyIdList;
	}
	/**
	 * @param tenancyIdList List of tenancy ID as Integer list
	 */
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		this.tenancyIdList = tenancyIdList;
	}
	
		
}
