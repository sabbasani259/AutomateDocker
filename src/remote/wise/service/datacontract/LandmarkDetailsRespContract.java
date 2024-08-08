package remote.wise.service.datacontract;

public class LandmarkDetailsRespContract {
	String Login_id;
	//private int Tenancy_ID;

	private int Landmark_id;
	private int Landmark_Category_ID;
	private String Landmark_Name;
	private String Latitude;
	private String Longitude;
	private double Radius;
	private String Address;	
	private int IsArrival;
	private int IsDeparture;
	private String Landmark_Category_Name;
//Added by Juhi on 24 july 2013
	private String Landmark_Category_Color_Code;
	
	public String getLandmark_Category_Color_Code() {
		return Landmark_Category_Color_Code;
	}
	public void setLandmark_Category_Color_Code(String landmark_Category_Color_Code) {
		Landmark_Category_Color_Code = landmark_Category_Color_Code;
	}
	public String getLandmark_Category_Name() {
		return Landmark_Category_Name;
	}
	public void setLandmark_Category_Name(String landmark_Category_Name) {
		Landmark_Category_Name = landmark_Category_Name;
	}

	public String getLogin_id() {
		return Login_id;
	}
	public void setLogin_id(String login_id) {
		Login_id = login_id;
	}
	public int getLandmark_id() {
		return Landmark_id;
	}
	public void setLandmark_id(int landmark_id) {
		Landmark_id = landmark_id;
	}
	public int getLandmark_Category_ID() {
		return Landmark_Category_ID;
	}
	public void setLandmark_Category_ID(int landmark_Category_ID) {
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
}
