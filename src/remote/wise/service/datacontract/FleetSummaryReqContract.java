package remote.wise.service.datacontract;

import java.util.List;

public class FleetSummaryReqContract {
	
	private String period;
	private String contactId;
	
	private List<Integer> tenancyIdList;
	private List<Integer> assetGroupIdList;
	private List<Integer> assetTypeIdList;
	private List<Integer> customAssetGroupIdList;
	private List<Integer> landmarkIdList;
	private List<Integer> alertTypeIdList;//notificationTypeId
	private List<String> alertSeverity;//notificationId
	private List<Integer> machineGroupId;
	private List<Integer> notificationDimensionID;
	private boolean isOwnStock; 
	
	
	public boolean isOwnStock() {
		return isOwnStock;
	}
	public void setOwnStock(boolean isOwnStock) {
		this.isOwnStock = isOwnStock;
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
	public List<Integer> getCustomAssetGroupIdList() {
		return customAssetGroupIdList;
	}
	public void setCustomAssetGroupIdList(List<Integer> customAssetGroupIdList) {
		this.customAssetGroupIdList = customAssetGroupIdList;
	}
	public List<Integer> getLandmarkIdList() {
		return landmarkIdList;
	}
	public void setLandmarkIdList(List<Integer> landmarkIdList) {
		this.landmarkIdList = landmarkIdList;
	}
	public List<Integer> getAlertTypeIdList() {
		return alertTypeIdList;
	}
	public void setAlertTypeIdList(List<Integer> alertTypeIdList) {
		this.alertTypeIdList = alertTypeIdList;
	}
	public List<String> getAlertSeverity() {
		return alertSeverity;
	}
	public void setAlertSeverity(List<String> alertSeverity) {
		this.alertSeverity = alertSeverity;
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
	public List<Integer> getMachineGroupId() {
		return machineGroupId;
	}
	public void setMachineGroupId(List<Integer> machineGroupId) {
		this.machineGroupId = machineGroupId;
	}
	public List<Integer> getNotificationDimensionID() {
		return notificationDimensionID;
	}
	public void setNotificationDimensionID(List<Integer> notificationDimensionID) {
		this.notificationDimensionID = notificationDimensionID;
	}

}
