package remote.wise.pojo;

public class GeofenceReportPOJO 
{
	String AssetID;
	String CustomerCode;
	String LandmarkName;
	String Latitude;
	String Longitude;
	double Radius;
	String Address;
	String IsArrival;
	String IsDeparture;
	String MobileNumber;
	boolean DayTimeSMSPref;
	boolean DayTimeWhatsAppPref;
	boolean DayTimeVoiceCallPref;
	boolean DayTimePushNotification;
	
	public String getAssetID() {
		return AssetID;
	}
	public void setAssetID(String assetID) {
		AssetID = assetID;
	}
	public String getCustomerCode() {
		return CustomerCode;
	}
	public void setCustomerCode(String customerCode) {
		CustomerCode = customerCode;
	}
	public String getLandmarkName() {
		return LandmarkName;
	}
	public void setLandmarkName(String landmarkName) {
		LandmarkName = landmarkName;
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
	
	public String getIsArrival() {
		return IsArrival;
	}
	public void setIsArrival(String isArrival) {
		IsArrival = isArrival;
	}
	public String getIsDeparture() {
		return IsDeparture;
	}
	public void setIsDeparture(String isDeparture) {
		IsDeparture = isDeparture;
	}
	public String getMobileNumber() {
		return MobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}
	public boolean isDayTimeSMSPref() {
		return DayTimeSMSPref;
	}
	public void setDayTimeSMSPref(boolean dayTimeSMSPref) {
		DayTimeSMSPref = dayTimeSMSPref;
	}
	public boolean isDayTimeWhatsAppPref() {
		return DayTimeWhatsAppPref;
	}
	public void setDayTimeWhatsAppPref(boolean dayTimeWhatsAppPref) {
		DayTimeWhatsAppPref = dayTimeWhatsAppPref;
	}
	public boolean isDayTimeVoiceCallPref() {
		return DayTimeVoiceCallPref;
	}
	public void setDayTimeVoiceCallPref(boolean dayTimeVoiceCallPref) {
		DayTimeVoiceCallPref = dayTimeVoiceCallPref;
	}
	public boolean isDayTimePushNotification() {
		return DayTimePushNotification;
	}
	public void setDayTimePushNotification(boolean dayTimePushNotification) {
		DayTimePushNotification = dayTimePushNotification;
	}
	
	
}
