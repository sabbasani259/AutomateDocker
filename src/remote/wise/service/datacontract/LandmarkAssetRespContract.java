package remote.wise.service.datacontract;

import java.util.List;

public class LandmarkAssetRespContract {
	private int Landmark_id;
	private List<String> Serial_number;
	//private String Serial_number;
	private List<Integer> asset_group_id;
	
	private String Login_Id;
	//private List<String> assetGroupName;
	

	public List<Integer> getAsset_group_id() {
		return asset_group_id;
	}
	public void setAsset_group_id(List<Integer> asset_group_id) {
		this.asset_group_id = asset_group_id;
	}
	
	
	
	public String getLogin_Id() {
		return Login_Id;
	}
	public void setLogin_Id(String login_Id) {
		Login_Id = login_Id;
	}
	
	
	
	/*public List<String> getAssetGroupName() {
		return assetGroupName;
	}
	public void setAssetGroupName(List<String> assetGroupName) {
		this.assetGroupName = assetGroupName;
	}*/
	public int getLandmark_id() {
		return Landmark_id;
	}
	public void setLandmark_id(int landmark_id) {
		Landmark_id = landmark_id;
	}
	/*public String getSerial_number() {
		return Serial_number;
	}
	public void setSerial_number(String serial_number) {
		Serial_number = serial_number;
	}
	*/
	public List<String> getSerial_number() {
		return Serial_number;
	}
	public void setSerial_number(List<String> serial_number) {
		Serial_number = serial_number;
	}

	
}
