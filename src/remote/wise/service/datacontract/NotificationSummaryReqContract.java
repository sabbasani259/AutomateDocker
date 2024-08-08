package remote.wise.service.datacontract;

import java.util.List;

public class NotificationSummaryReqContract {
	
	private String period;
	private String contactId;

	private List<Integer> notificationTypeIdList;
   private List<Integer> notificationIdList;    
   private List<Integer> tenancyIdList;     
   private List<Integer>  assetGroupIdList;  
   private List<Integer>  assetTypeIdList;  
   private List<Integer> eventTypeIdList;
   private List<String> eventSeverityList;
   private boolean ownStock;	
   private boolean activeAlerts;
	
	public boolean isOwnStock() {
		return ownStock;
	}
	public void setOwnStock(boolean ownStock) {
		this.ownStock = ownStock;
	}
   
  
   public List<Integer> getEventTypeIdList() {
	return eventTypeIdList;
}
public void setEventTypeIdList(List<Integer> eventTypeIdList) {
	this.eventTypeIdList = eventTypeIdList;
}
public List<String> getEventSeverityList() {
	return eventSeverityList;
}
public void setEventSeverityList(List<String> eventSeverityList) {
	this.eventSeverityList = eventSeverityList;
}
public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public List<Integer> getNotificationTypeIdList() {
		return notificationTypeIdList;
	}
	public void setNotificationTypeIdList(List<Integer> notificationTypeIdList) {
		this.notificationTypeIdList = notificationTypeIdList;
	}
	public List<Integer> getNotificationIdList() {
		return notificationIdList;
	}
	public void setNotificationIdList(List<Integer> notificationIdList) {
		this.notificationIdList = notificationIdList;
	}
	public List<Integer> getTenancyIdList() {
		return tenancyIdList;
	}
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		this.tenancyIdList = tenancyIdList;
	}
	public List<Integer> getAssetGroupIdList() {
		return assetGroupIdList;
	}
	public void setAssetGroupIdList(List<Integer> assetGroupIdList) {
		this.assetGroupIdList = assetGroupIdList;
	}
	public List<Integer> getAssetTypeIdList() {
		return assetTypeIdList;
	}
	public void setAssetTypeIdList(List<Integer> assetTypeIdList) {
		this.assetTypeIdList = assetTypeIdList;
	}
	/*public List<Integer> getCustomAssetGroupIdList() {
		return customAssetGroupIdList;
	}
	public void setCustomAssetGroupIdList(List<Integer> customAssetGroupIdList) {
		this.customAssetGroupIdList = customAssetGroupIdList;
	}*/
	public boolean isActiveAlerts() {
		return activeAlerts;
	}
	public void setActiveAlerts(boolean activeAlerts) {
		this.activeAlerts = activeAlerts;
	}
	

}
