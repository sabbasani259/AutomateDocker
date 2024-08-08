package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class TenancyCreationReqContract 
{
	String loginId;
	int accountId;
	int parentTenancyId;
	int childTenancyId;
	String childTenancyName;
	String operatingStartTime;
	String operatingEndTime;
	
	String tenancyAdminFirstName;
	String tenancyAdminLastName;
	String tenancyAdminEmailId;
	String tenancyAdminPhoneNumber;
	int tenancyAdminRoleId;
	
	List<String> parentTenancyUserIdList;
	boolean overrideMachineOperatingHours;
	String countryCode;
	
	public TenancyCreationReqContract()
	{
		loginId = null;
		accountId =0;
		parentTenancyId =0;
		childTenancyId=0;
		childTenancyName = null;
		operatingStartTime = null;
		operatingEndTime = null;
		tenancyAdminFirstName =null;
		tenancyAdminLastName = null;
		tenancyAdminEmailId = null;
		tenancyAdminPhoneNumber = null;
		tenancyAdminRoleId =0;
		parentTenancyUserIdList = null;
		overrideMachineOperatingHours = false;
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
	 * @return the accountId
	 */
	public int getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the parentTenancyId
	 */
	public int getParentTenancyId() {
		return parentTenancyId;
	}

	/**
	 * @param parentTenancyId the parentTenancyId to set
	 */
	public void setParentTenancyId(int parentTenancyId) {
		this.parentTenancyId = parentTenancyId;
	}

	/**
	 * @return the childTenancyId
	 */
	public int getChildTenancyId() {
		return childTenancyId;
	}

	/**
	 * @param childTenancyId the childTenancyId to set
	 */
	public void setChildTenancyId(int childTenancyId) {
		this.childTenancyId = childTenancyId;
	}

	/**
	 * @return the childTenancyName
	 */
	public String getChildTenancyName() {
		return childTenancyName;
	}

	/**
	 * @param childTenancyName the childTenancyName to set
	 */
	public void setChildTenancyName(String childTenancyName) {
		this.childTenancyName = childTenancyName;
	}

	/**
	 * @return the operatingStartTime
	 */
	public String getOperatingStartTime() {
		return operatingStartTime;
	}

	/**
	 * @param operatingStartTime the operatingStartTime to set
	 */
	public void setOperatingStartTime(String operatingStartTime) {
		this.operatingStartTime = operatingStartTime;
	}

	/**
	 * @return the operatingEndTime
	 */
	public String getOperatingEndTime() {
		return operatingEndTime;
	}

	/**
	 * @param operatingEndTime the operatingEndTime to set
	 */
	public void setOperatingEndTime(String operatingEndTime) {
		this.operatingEndTime = operatingEndTime;
	}

	/**
	 * @return the tenancyAdminFirstName
	 */
	public String getTenancyAdminFirstName() {
		return tenancyAdminFirstName;
	}

	/**
	 * @param tenancyAdminFirstName the tenancyAdminFirstName to set
	 */
	public void setTenancyAdminFirstName(String tenancyAdminFirstName) {
		this.tenancyAdminFirstName = tenancyAdminFirstName;
	}

	/**
	 * @return the tenancyAdminLastName
	 */
	public String getTenancyAdminLastName() {
		return tenancyAdminLastName;
	}

	/**
	 * @param tenancyAdminLastName the tenancyAdminLastName to set
	 */
	public void setTenancyAdminLastName(String tenancyAdminLastName) {
		this.tenancyAdminLastName = tenancyAdminLastName;
	}

	/**
	 * @return the tenancyAdminEmailId
	 */
	public String getTenancyAdminEmailId() {
		return tenancyAdminEmailId;
	}

	/**
	 * @param tenancyAdminEmailId the tenancyAdminEmailId to set
	 */
	public void setTenancyAdminEmailId(String tenancyAdminEmailId) {
		this.tenancyAdminEmailId = tenancyAdminEmailId;
	}

	/**
	 * @return the tenancyAdminPhoneNumber
	 */
	public String getTenancyAdminPhoneNumber() {
		return tenancyAdminPhoneNumber;
	}

	/**
	 * @param tenancyAdminPhoneNumber the tenancyAdminPhoneNumber to set
	 */
	public void setTenancyAdminPhoneNumber(String tenancyAdminPhoneNumber) {
		this.tenancyAdminPhoneNumber = tenancyAdminPhoneNumber;
	}

	/**
	 * @return the tenancyAdminRoleId
	 */
	public int getTenancyAdminRoleId() {
		return tenancyAdminRoleId;
	}

	/**
	 * @param tenancyAdminRoleId the tenancyAdminRoleId to set
	 */
	public void setTenancyAdminRoleId(int tenancyAdminRoleId) {
		this.tenancyAdminRoleId = tenancyAdminRoleId;
	}

	/**
	 * @return the parentTenancyUserIdList
	 */
	public List<String> getParentTenancyUserIdList() {
		return parentTenancyUserIdList;
	}

	/**
	 * @param parentTenancyUserIdList the parentTenancyUserIdList to set
	 */
	public void setParentTenancyUserIdList(List<String> parentTenancyUserIdList) {
		this.parentTenancyUserIdList = parentTenancyUserIdList;
	}

	/**
	 * @return the overrideMachineOperatingHours
	 */
	public boolean isOverrideMachineOperatingHours() {
		return overrideMachineOperatingHours;
	}

	/**
	 * @param overrideMachineOperatingHours the overrideMachineOperatingHours to set
	 */
	public void setOverrideMachineOperatingHours(
			boolean overrideMachineOperatingHours) {
		this.overrideMachineOperatingHours = overrideMachineOperatingHours;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
}
