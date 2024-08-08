package remote.wise.businessentity;

import java.io.Serializable;

public class CustomAssetGroupSnapshotEntity extends BaseBusinessEntity implements Serializable{

	private int Group_ID;
	private String user_Id;
	private String Asset_Id;
	public String getUser_Id() {
		return user_Id;
	}
	public void setUser_Id(String user_Id) {
		this.user_Id = user_Id;
	}
	public String getAsset_Id() {
		return Asset_Id;
	}
	public void setAsset_Id(String asset_Id) {
		Asset_Id = asset_Id;
	}
	public int getGroup_ID() {
		return Group_ID;
	}
	public void setGroup_ID(int group_ID) {
		Group_ID = group_ID;
	}
	
}
