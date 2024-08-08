package remote.wise.service.datacontract;


public class MachineProfileReqContract {
	private String loginID;
	private int assetGroupId;
	
	public int getAssetGroupId() {
		return assetGroupId;
	}

	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}

	public String getLoginId() {
		return loginID;
	}

	public void setLoginId(String loginId) {
		this.loginID = loginId;
	}

}
