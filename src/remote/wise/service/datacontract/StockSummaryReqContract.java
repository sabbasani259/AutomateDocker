package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class StockSummaryReqContract 
{
	String loginId;
	int tenancyId;
	
	
	/**
	 * @return userLoginId
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
	 * @return TenancyId
	 */
	public int getTenancyId() {
		return tenancyId;
	}
	/**
	 * @param tenancyId TenancyId as Integer input
	 */
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	
}
