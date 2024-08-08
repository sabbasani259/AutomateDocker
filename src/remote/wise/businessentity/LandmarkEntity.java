package remote.wise.businessentity;

import java.io.Serializable;

public class LandmarkEntity  extends BaseBusinessEntity 
{
	private int Landmark_id;
	private LandmarkCategoryEntity Landmark_Category_ID;
	private String Landmark_Name;
	private String Latitude;
	private String Longitude;
	private double Radius;
	private String Address;	
	private int IsArrival;
	private int IsDeparture;
	private int ActiveStatus;
	private String country_code;


	public String getCountry_code() {
		return country_code;
	}
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}
	public String getLogin_Id() {
		return Login_Id;
	}
	public void setLogin_Id(String login_Id) {
		Login_Id = login_Id;
	}
	private String Login_Id;
	
	public int getLandmark_id() {
		return Landmark_id;
	}
	public void setLandmark_id(int landmark_id) {
		Landmark_id = landmark_id;
	}
	public LandmarkCategoryEntity getLandmark_Category_ID() {
		return Landmark_Category_ID;
	}
	public void setLandmark_Category_ID(LandmarkCategoryEntity landmark_Category_ID) {
		Landmark_Category_ID = landmark_Category_ID;
	}
	public String getLandmark_Name() {
		return Landmark_Name;
	}
	public void setLandmark_Name(String landmark_Name) {
		Landmark_Name = landmark_Name;
	}
	public String getLatitude() {
		return Latitude;
	}
	public void setLatitude(String latitude) {
		Latitude = latitude;
	}
	public String getLongitude() {
		return Longitude;
	}
	public void setLongitude(String longitude) {
		Longitude = longitude;
	}
	public double getRadius() {
		return Radius;
	}
	public void setRadius(double radius) {
		Radius = radius;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public int getIsArrival() {
		return IsArrival;
	}
	public void setIsArrival(int isArrival) {
		IsArrival = isArrival;
	}
	public int getIsDeparture() {
		return IsDeparture;
	}
	public void setIsDeparture(int isDeparture) {
		IsDeparture = isDeparture;
	}
	public int getActiveStatus() {
		return ActiveStatus;
	}
	public void setActiveStatus(int activeStatus) {
		ActiveStatus = activeStatus;
	}
	public LandmarkEntity(){ }
	public LandmarkEntity(int Landmark_id) 
	{
		super.key = new Integer(Landmark_id);
		LandmarkEntity e = (LandmarkEntity)read(this);
		if(e!= null)
		{ 
			setLandmark_Category_ID(e.getLandmark_Category_ID());
			setLandmark_id(e.getLandmark_id());
			// DF20180920 :MANI: Adding new column country_code in landmark
			// table. getting the countrycode in landmark name with
			setLandmark_Name(e.getLandmark_Name());
			setLatitude(e.getLatitude());
			setLongitude(e.getLongitude());
			setRadius(e.getRadius());
			setAddress(e.getAddress());
			setIsArrival(e.getIsArrival());
			setIsDeparture(e.getIsDeparture());
			setLogin_Id(e.getLogin_Id());
			setCountry_code(e.getCountry_code());

		}
	}

		
	

}
