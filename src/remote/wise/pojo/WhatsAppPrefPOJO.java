package remote.wise.pojo;

import java.util.HashMap;

public class WhatsAppPrefPOJO 
{
	String AssetID;
	HashMap<String,String> NotificationTimeSlot;
	String MobileNumber;
	String MachineDetails;
	
	public String getAssetID() {
		return AssetID;
	}
	public void setAssetID(String assetID) {
		AssetID = assetID;
	}
	
	public HashMap<String, String> getNotificationTimeSlot() {
		return NotificationTimeSlot;
	}
	public void setNotificationTimeSlot(HashMap<String, String> notificationTimeSlot) {
		NotificationTimeSlot = notificationTimeSlot;
	}
	public String getMobileNumber() {
		return MobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}
	public String getMachineDetails() {
		return MachineDetails;
	}
	public void setMachineDetails(String machineDetails) {
		MachineDetails = machineDetails;
	}
	
	
	
}
