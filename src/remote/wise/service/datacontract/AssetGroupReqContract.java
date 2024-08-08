package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class AssetGroupReqContract 
{
	String loginId;
	List<Integer> tenancyIdList;
	int assetGroupId;
	int assetGroupTypeId;
	List<String> serialNumberList;
	boolean isOtherTenancy;
	private String message;
	
	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public AssetGroupReqContract()
	{
		loginId = null;
		tenancyIdList = null;
		assetGroupId = 0;
		assetGroupTypeId = 0;
		serialNumberList = null;
		isOtherTenancy = false;
	}


	/**
	 * @return userLoginId as String
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
	 * @return list of tenancyId as Integer list
	 */
	public List<Integer> getTenancyIdList() {
		return tenancyIdList;
	}
	/**
	 * @param tenancyIdList List of TenancyId as Integer input
	 */
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		this.tenancyIdList = tenancyIdList;
	}


	/**
	 * @return custom machine groupId as Integer
	 */
	public int getAssetGroupId() {
		return assetGroupId;
	}
	/**
	 * @param assetGroupId customMachineGroupId as Integer input
	 */
	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}


	
	/**
	 * @return assetGroupTypeId as Integer
	 */
	public int getAssetGroupTypeId() {
		return assetGroupTypeId;
	}
	/**
	 * @param assetGroupTypeId AssetGroupTypeId as Integer input
	 */
	public void setAssetGroupTypeId(int assetGroupTypeId) {
		this.assetGroupTypeId = assetGroupTypeId;
	}

	
	/**
	 * @return List of serialNumbers
	 */
	public List<String> getSerialNumberList() {
		return serialNumberList;
	}
	/**
	 * @param serialNumberList  List of serialNumber as String list
	 */
	public void setSerialNumberList(List<String> serialNumberList) {
		this.serialNumberList = serialNumberList;
	}


	
	/**
	 * @return Returns the boolean information of is it OtherTenancy
	 */
	public boolean isOtherTenancy() {
		return isOtherTenancy;
	}
	/**
	 * @param isOtherTenancy Boolean information of is it other tenancy
	 */
	public void setOtherTenancy(boolean isOtherTenancy) {
		this.isOtherTenancy = isOtherTenancy;
	}
	
	
}
