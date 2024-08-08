package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class AssetGroupTypeReqContract 
{
	String loginId;
	List<Integer> tenancyIdList;
	int assetGroupTypeId;
	boolean isOtherTenancy;
	
	public AssetGroupTypeReqContract()
	{
		loginId = null;
		tenancyIdList = null;
		assetGroupTypeId =0;
		isOtherTenancy = false;
	}
	
	
	/**
	 * @return Returns the userLoginId
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId User LoginId as input String
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	
	/**
	 * @return Returns the list of tenancyId
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
	 * @return Returns the Custom Group Type Id
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
