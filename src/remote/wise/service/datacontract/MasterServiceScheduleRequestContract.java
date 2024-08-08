package remote.wise.service.datacontract;

public class MasterServiceScheduleRequestContract {
	private int assetTypeId;
    private int engineTypeId;
    private int assetGroupId;
    private String scheduleName;
	public int getAssetTypeId() {
		return assetTypeId;
	}
	public void setAssetTypeId(int assetTypeId) {
		this.assetTypeId = assetTypeId;
	}
	public int getEngineTypeId() {
		return engineTypeId;
	}
	public void setEngineTypeId(int engineTypeId) {
		this.engineTypeId = engineTypeId;
	}
	public int getAssetGroupId() {
		return assetGroupId;
	}
	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}  
}
