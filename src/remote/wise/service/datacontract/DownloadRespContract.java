package remote.wise.service.datacontract;

public class DownloadRespContract {
	private int LandmarkCategory_id;
	private String Landmark_Category_Name;
	private String Color_Code;
	private int Tenancy_ID;
	public int getLandmarkCategory_id() {
		return LandmarkCategory_id;
	}
	public void setLandmarkCategory_id(int landmarkCategory_id) {
		LandmarkCategory_id = landmarkCategory_id;
	}
	public String getLandmark_Category_Name() {
		return Landmark_Category_Name;
	}
	public void setLandmark_Category_Name(String landmark_Category_Name) {
		Landmark_Category_Name = landmark_Category_Name;
	}
	public String getColor_Code() {
		return Color_Code;
	}
	public void setColor_Code(String color_Code) {
		Color_Code = color_Code;
	}
	public int getTenancy_ID() {
		return Tenancy_ID;
	}
	public void setTenancy_ID(int tenancy_ID) {
		Tenancy_ID = tenancy_ID;
	}
	
}
