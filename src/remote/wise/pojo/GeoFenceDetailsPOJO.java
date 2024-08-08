package remote.wise.pojo;

import java.util.HashMap;

public class GeoFenceDetailsPOJO {
	
	String LandmarkID;
	String LandmarkCategoryID;
	String LandmarkName;
	String LandmarkCategoryName;
	String VIN;
	String CreatedDate;
	String CustomerCode;
	String MobileNumber;
	HashMap<String,Object>  GeofenceDetails;
	HashMap<String,Object>  DayTimePreference;
	HashMap<String,Object>  OtherTimePreference;
	
	public String getLandmarkID() {
		return LandmarkID;
	}
	public void setLandmarkID(String landmarkID) {
		LandmarkID = landmarkID;
	}
	public String getLandmarkCategoryID() {
		return LandmarkCategoryID;
	}
	public void setLandmarkCategoryID(String landmarkCategoryID) {
		LandmarkCategoryID = landmarkCategoryID;
	}
	public String getLandmarkName() {
		return LandmarkName;
	}
	public void setLandmarkName(String landmarkName) {
		LandmarkName = landmarkName;
	}
	public String getLandmarkCategoryName() {
		return LandmarkCategoryName;
	}
	public void setLandmarkCategoryName(String landmarkCategoryName) {
		LandmarkCategoryName = landmarkCategoryName;
	}
	public String getVIN() {
		return VIN;
	}
	public void setVIN(String vIN) {
		VIN = vIN;
	}
	public String getCreatedDate() {
		return CreatedDate;
	}
	public void setCreatedDate(String createdDate) {
		CreatedDate = createdDate;
	}
	public String getCustomerCode() {
		return CustomerCode;
	}
	public void setCustomerCode(String customerCode) {
		CustomerCode = customerCode;
	}
	public String getMobileNumber() {
		return MobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}
	public HashMap<String, Object> getGeofenceDetails() {
		return GeofenceDetails;
	}
	public void setGeofenceDetails(HashMap<String, Object> geofenceDetails) {
		GeofenceDetails = geofenceDetails;
	}
	public HashMap<String, Object> getDayTimePreference() {
		return DayTimePreference;
	}
	public void setDayTimePreference(HashMap<String, Object> dayTimePreference) {
		DayTimePreference = dayTimePreference;
	}
	public HashMap<String, Object> getOtherTimePreference() {
		return OtherTimePreference;
	}
	public void setOtherTimePreference(HashMap<String, Object> otherTimePreference) {
		OtherTimePreference = otherTimePreference;
	}

}
