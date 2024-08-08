package remote.wise.pojo;

import java.util.HashMap;

import org.json.JSONObject;

public class TimeFenceDetailsPOJO 
{
	String VIN;
	String CreatedDate;
	String CustomerCode;
	String OperatingStartTime;
	String OperatingEndtime;
	String MobileNumber;
	HashMap<String,Object>  TimefenceDetails;
	HashMap<String,Object> DayTimePreference;
	HashMap<String,Object> OtherTimePreference;
	
	
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
	public String getOperatingStartTime() {
		return OperatingStartTime;
	}
	public void setOperatingStartTime(String operatingStartTime) {
		OperatingStartTime = operatingStartTime;
	}
	public String getOperatingEndtime() {
		return OperatingEndtime;
	}
	public void setOperatingEndtime(String operatingEndtime) {
		OperatingEndtime = operatingEndtime;
	}
	public String getMobileNumber() {
		return MobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}
	public HashMap<String, Object> getTimefenceDetails() {
		return TimefenceDetails;
	}
	public void setTimefenceDetails(HashMap<String, Object> timefenceDetails) {
		TimefenceDetails = timefenceDetails;
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
