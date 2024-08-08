package remote.wise.pojo;

import java.util.HashMap;

public class TimefenceReportPOJO 
{
	String AssetID;
	String CustomerCode;
	String OperatingStartTime;
	String OperatingEndTime;
	String NotificationPattern;
	String NotificationDate;
	HashMap<String,Object> RecurrencePattern;
	String StartDate;
	String EndDate;
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
	public String getOperatingStartTime() {
		return OperatingStartTime;
	}
	public void setOperatingStartTime(String operatingStartTime) {
		OperatingStartTime = operatingStartTime;
	}
	public String getOperatingEndTime() {
		return OperatingEndTime;
	}
	public void setOperatingEndTime(String operatingEndTime) {
		OperatingEndTime = operatingEndTime;
	}
	public String getNotificationPattern() {
		return NotificationPattern;
	}
	public void setNotificationPattern(String notificationPattern) {
		NotificationPattern = notificationPattern;
	}
	public String getNotificationDate() {
		return NotificationDate;
	}
	public void setNotificationDate(String notificationDate) {
		NotificationDate = notificationDate;
	}
	
	public HashMap<String, Object> getRecurrencePattern() {
		return RecurrencePattern;
	}
	public void setRecurrencePattern(HashMap<String, Object> recurrencePattern) {
		RecurrencePattern = recurrencePattern;
	}
	public String getStartDate() {
		return StartDate;
	}
	public void setStartDate(String startDate) {
		StartDate = startDate;
	}
	public String getEndDate() {
		return EndDate;
	}
	public void setEndDate(String endDate) {
		EndDate = endDate;
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
