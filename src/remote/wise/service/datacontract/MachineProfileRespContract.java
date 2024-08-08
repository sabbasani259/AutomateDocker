package remote.wise.service.datacontract;

public class MachineProfileRespContract {
	
	private int assetGroupId;
	private String asseetOperatingStartTime;
	private String asseetOperatingEndTime;
	private String profileName;
	private String assetGroupCode;

	public String getAssetGroupCode() {
		return assetGroupCode;
	}

	public void setAssetGroupCode(String assetGroupCode) {
		this.assetGroupCode = assetGroupCode;
	}

	public int getAssetGroupId() {
		return assetGroupId;
	}

	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}

	public String getAsseetOperatingStartTime() {
		return asseetOperatingStartTime;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public void setAsseetOperatingStartTime(String asseetOperatingStartTime) {
		this.asseetOperatingStartTime = asseetOperatingStartTime;
	}

	public String getAsseetOperatingEndTime() {
		return asseetOperatingEndTime;
	}

	public void setAsseetOperatingEndTime(String asseetOperatingEndTime) {
		this.asseetOperatingEndTime = asseetOperatingEndTime;
	}

}
