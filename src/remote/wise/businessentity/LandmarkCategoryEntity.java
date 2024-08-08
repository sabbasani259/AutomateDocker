package remote.wise.businessentity;

import java.io.Serializable;

public class LandmarkCategoryEntity extends BaseBusinessEntity implements Serializable
{
	private int Landmark_Category_ID;
	private String Landmark_Category_Name;
	private String Color_Code;
	private TenancyEntity Tenancy_ID;
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

	public LandmarkCategoryEntity(){ }

	public LandmarkCategoryEntity(int landmark_Category_ID) 
	{
		super.key = new Integer(landmark_Category_ID);
		LandmarkCategoryEntity e = (LandmarkCategoryEntity)read(this);
		if(e!= null)
		{ 
			setLandmark_Category_ID(e.getLandmark_Category_ID());
			setLandmark_Category_Name(e.getLandmark_Category_Name());
			setColor_Code(e.getColor_Code());
			setTenancy_ID(e.getTenancy_ID());
		
		}
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

	public TenancyEntity getTenancy_ID() {
		return Tenancy_ID;
	}

	public void setTenancy_ID(TenancyEntity tenancy_ID) {
		Tenancy_ID = tenancy_ID;
	}


}
