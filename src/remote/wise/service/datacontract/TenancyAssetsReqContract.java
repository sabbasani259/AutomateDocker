package remote.wise.service.datacontract;

import java.util.List;

public class TenancyAssetsReqContract 
{
	String loginId;
	List<Integer> tenancyIdList;
	boolean childTenancyAssetsRequired;
	String serialNumber;
	
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

	public TenancyAssetsReqContract()
	{
		loginId =null;
		tenancyIdList = null;
		childTenancyAssetsRequired = true;
		serialNumber =null;
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
	 * @return the childTenancyAssetsRequired
	 */
	public boolean isChildTenancyAssetsRequired() {
		return childTenancyAssetsRequired;
	}

	/**
	 * @param childTenancyAssetsRequired the childTenancyAssetsRequired to set
	 */
	public void setChildTenancyAssetsRequired(boolean childTenancyAssetsRequired) {
		this.childTenancyAssetsRequired = childTenancyAssetsRequired;
	}
	
	
	
}
