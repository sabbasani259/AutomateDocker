package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class UserAlertsReqContract 
{
	String loginId;
	int roleId;
	List<Integer> userTenancyIdList;
	List<Integer> tenancyIdList;
	String serialNumber;
	String startDate;
	List<Integer> alertTypeId;
	List<String> alertSeverity;
	boolean isOwnTenancyAlerts;
	boolean isHistory;
	//DefectId:20150416 @Suprava Nayak 50 record per page Changes
	int pageNumber;
	
	/**
	 * @return the pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}


	/**
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}


	public UserAlertsReqContract()
	{
		loginId=null;
		userTenancyIdList =null;
		tenancyIdList =null;
		serialNumber =null;
		startDate =null;
		alertTypeId =null;
		alertSeverity =null;
		isOwnTenancyAlerts = true;
		isHistory = false;
		
	}


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
	 * @return the roleId
	 */
	public int getRoleId() {
		return roleId;
	}


	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}


	/**
	 * @return the userTenancyIdList
	 */
	public List<Integer> getUserTenancyIdList() {
		return userTenancyIdList;
	}


	/**
	 * @param userTenancyIdList the userTenancyIdList to set
	 */
	public void setUserTenancyIdList(List<Integer> userTenancyIdList) {
		this.userTenancyIdList = userTenancyIdList;
	}


	/**
	 * @return the tenancyIdList
	 */
	public List<Integer> getTenancyIdList() {
		return tenancyIdList;
	}


	/**
	 * @param tenancyIdList the tenancyIdList to set
	 */
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		this.tenancyIdList = tenancyIdList;
	}


	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}


	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}


	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the alertTypeId
	 */
	public List<Integer> getAlertTypeId() {
		return alertTypeId;
	}


	/**
	 * @param alertTypeId the alertTypeId to set
	 */
	public void setAlertTypeId(List<Integer> alertTypeId) {
		this.alertTypeId = alertTypeId;
	}


	/**
	 * @return the alertSeverity
	 */
	public List<String> getAlertSeverity() {
		return alertSeverity;
	}


	/**
	 * @param alertSeverity the alertSeverity to set
	 */
	public void setAlertSeverity(List<String> alertSeverity) {
		this.alertSeverity = alertSeverity;
	}


	/**
	 * @return the isOwnTenancyAlerts
	 */
	public boolean isOwnTenancyAlerts() {
		return isOwnTenancyAlerts;
	}


	/**
	 * @param isOwnTenancyAlerts the isOwnTenancyAlerts to set
	 */
	public void setOwnTenancyAlerts(boolean isOwnTenancyAlerts) {
		this.isOwnTenancyAlerts = isOwnTenancyAlerts;
	}


	/**
	 * @return the isHistory
	 */
	public boolean isHistory() {
		return isHistory;
	}


	/**
	 * @param isHistory the isHistory to set
	 */
	public void setHistory(boolean isHistory) {
		this.isHistory = isHistory;
	}
	
	
	
}
