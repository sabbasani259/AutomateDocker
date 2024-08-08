package remote.wise.service.datacontract;

import java.util.List;

public class TenancyDetailsReqContract 
{
	String loginId;
	List<Integer> parentTenancyIdList;
	List<Integer> childTenancyIdList;
	
	public TenancyDetailsReqContract()
	{
		loginId =null;
		parentTenancyIdList = null;
		childTenancyIdList = null;
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
	 * @return the parentTenancyIdList
	 */
	public List<Integer> getParentTenancyIdList() {
		return parentTenancyIdList;
	}

	/**
	 * @param parentTenancyIdList the parentTenancyIdList to set
	 */
	public void setParentTenancyIdList(List<Integer> parentTenancyIdList) {
		this.parentTenancyIdList = parentTenancyIdList;
	}

	/**
	 * @return the childTenancyIdList
	 */
	public List<Integer> getChildTenancyIdList() {
		return childTenancyIdList;
	}

	/**
	 * @param childTenancyIdList the childTenancyIdList to set
	 */
	public void setChildTenancyIdList(List<Integer> childTenancyIdList) {
		this.childTenancyIdList = childTenancyIdList;
	}
	
	
}
