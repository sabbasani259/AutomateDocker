package remote.wise.service.datacontract;

import java.util.List;

public class UserDetailsReqContract{

	int tenancy_id;
	String userId;
	int AssetGroupId;
	List<Integer> tenacyIdList;
	String isMACustConsolidatedLogin;
	
	
	public String getMACustConsolidatedLogin() {
		return isMACustConsolidatedLogin;
	}

	public void setMACustConsolidatedLogin(String isMACustConsolidatedLogin2) {
		this.isMACustConsolidatedLogin = isMACustConsolidatedLogin2;
	}
	public int getAssetGroupId() {
	return AssetGroupId;
}

public void setAssetGroupId(int assetGroupId) {
	AssetGroupId = assetGroupId;
}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getTenancy_id() {
		return tenancy_id;
	}

	public void setTenancy_id(int tenancy_id) {
		this.tenancy_id = tenancy_id;
	}

	public List<Integer> getTenacyIdList() {
		return tenacyIdList;
	}

	public void setTenacyIdList(List<Integer> tenacyIdList) {
		this.tenacyIdList = tenacyIdList;
	}
	
	
}
