package remote.wise.businessentity;

public class LandmarkCategory2
{
	private int Landmark_Category_ID;
	private String Landmark_Category_Name;
	private String Color_Code;
	private int Tenancy_ID;
	private int ActiveStatus;
	private String Landmark_Category_Color_Code;
	public String getLandmark_Category_Color_Code() {
		return Landmark_Category_Color_Code;
	}

	public void setLandmark_Category_Color_Code(String landmark_Category_Color_Code) {
		Landmark_Category_Color_Code = landmark_Category_Color_Code;
	}

	public int getActiveStatus() {
		return ActiveStatus;
	}

	public void setActiveStatus(int activeStatus) {
		ActiveStatus = activeStatus;
	}


	public int getLandmark_Category_ID() {
		return Landmark_Category_ID;
	}

	public void setLandmark_Category_ID(int landmark_Category_ID) {
		Landmark_Category_ID = landmark_Category_ID;
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
