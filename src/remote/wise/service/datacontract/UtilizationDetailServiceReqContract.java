package remote.wise.service.datacontract;

/**
 * @author Rajani Nagaraju
 *
 */
public class UtilizationDetailServiceReqContract 
{	
	String loginId;
	String serialNumber;
	String period;
	
	
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
	 * @return VIN
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber VIN as input string
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	/**
	 * @return Period - either of these strings: Today, Week, Month, Quarter, Year
	 */
	public String getPeriod() {
		return period;
	}
	/**
	 * @param period Period as input String - either of these:Today, Week, Month, Quarter, Year
	 */
	public void setPeriod(String period) {
		this.period = period;
	}
	
}	
