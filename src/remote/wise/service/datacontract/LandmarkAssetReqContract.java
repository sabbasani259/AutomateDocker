package remote.wise.service.datacontract;

import java.util.List;

public class LandmarkAssetReqContract {
	List<String> Serial_numbers;
	//List<Integer> asset_group_id;
	private	String login_id;
	private int Landmark_id;
	
	public List<String> getSerial_numbers() {
		return Serial_numbers;
	}
	public void setSerial_numbers(List<String> serial_numbers) {
		Serial_numbers = serial_numbers;
	}
	/*public List<Integer> getAsset_group_id() {
		return asset_group_id;
	}
	public void setAsset_group_id(List<Integer> asset_group_id) {
		this.asset_group_id = asset_group_id;
	}*/
	
	public String getLogin_id() {
		return login_id;
	}
	public void setLogin_id(String login_id) {
		this.login_id = login_id;
	}
	public int getLandmark_id() {
		return Landmark_id;
	}
	public void setLandmark_id(int landmark_id) {
		Landmark_id = landmark_id;
	}
	
	

}
