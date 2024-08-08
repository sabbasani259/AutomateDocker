package remote.wise.service.datacontract;

import java.util.HashMap;
import java.util.List;

/**
 * @author Rajani Nagaraju
 * DefectId: To be provided by testing team
 * Rajani Nagaraju - 20130704 - AccountName to be returned from TenancyDetailsService 
 */
public class TenancyDetailsRespContract 
{
	int tenancyId;
	String tenancyName;
	int parentTenancyId;
	String parentTenancyName;
	List<String> tenancyAdminList;
	HashMap<String,String> parentTenancyUserIdMailIdList;
	String createdBy;
	String createdDate;
	String operatingStartTime;
	String operatingEndTime;
	//DefectId: To be provided by testing team
	//Rajani Nagaraju - 20130704 - AccountName to be returned from TenancyDetailsService 
	String accountName;
	//DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
	private String tenancyCode;
	//DF20190516: Anudeep adding size to implement pagination
	int size;
	
	public TenancyDetailsRespContract()
	{
		tenancyId =0;
		tenancyName = null;
		parentTenancyId =0;
		parentTenancyName = null;
		tenancyAdminList = null;
		parentTenancyUserIdMailIdList = null;
		createdBy = null;
		createdDate = null;
		operatingStartTime = null;
		operatingEndTime = null;
		accountName=null;
		//DF20190516: Anudeep adding size to implement pagination
		size =0;
	}

	//DF20190516: Anudeep adding size to implement pagination
	public int getSize() {
		return size;
	}
	//DF20190516: Anudeep adding size to implement pagination
	public void setSize(int size) {
		this.size = size;
	}

	//DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
	public String getTenancyCode() {
		return tenancyCode;
	}

	public void setTenancyCode(String tenancyCode) {
		this.tenancyCode = tenancyCode;
	}
	
	
	
	/**
	 * @return the tenancyId
	 */
	public int getTenancyId() {
		return tenancyId;
	}

	

	/**
	 * @param tenancyId the tenancyId to set
	 */
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}

	/**
	 * @return the tenancyName
	 */
	public String getTenancyName() {
		return tenancyName;
	}

	/**
	 * @param tenancyName the tenancyName to set
	 */
	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
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
	 * @return the parentTenancyName
	 */
	public String getParentTenancyName() {
		return parentTenancyName;
	}

	/**
	 * @param parentTenancyName the parentTenancyName to set
	 */
	public void setParentTenancyName(String parentTenancyName) {
		this.parentTenancyName = parentTenancyName;
	}

	/**
	 * @return the tenancyAdminList
	 */
	public List<String> getTenancyAdminList() {
		return tenancyAdminList;
	}

	/**
	 * @param tenancyAdminList the tenancyAdminList to set
	 */
	public void setTenancyAdminList(List<String> tenancyAdminList) {
		this.tenancyAdminList = tenancyAdminList;
	}

	
	/**
	 * @return the parentTenancyUserIdMailIdList
	 */
	public HashMap<String, String> getParentTenancyUserIdMailIdList() {
		return parentTenancyUserIdMailIdList;
	}

	/**
	 * @param parentTenancyUserIdMailIdList the parentTenancyUserIdMailIdList to set
	 */
	public void setParentTenancyUserIdMailIdList(
			HashMap<String, String> parentTenancyUserIdMailIdList) {
		this.parentTenancyUserIdMailIdList = parentTenancyUserIdMailIdList;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
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

	//DefectId: To be provided by testing team
	//Rajani Nagaraju - 20130704 - AccountName to be returned from TenancyDetailsService 
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
}
	
