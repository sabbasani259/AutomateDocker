package remote.wise.service.datacontract;

import java.util.List;

public class AlertSummaryReqContract {
private String LoginId;
	
	private String Period;
	private List<Integer> loginTenancyIdList;
	private List<Integer> TenancyIdList;
	private List<Integer> AssetGroupIdList; 
	private List<Integer> AssetTypeIdList;
	private List<Integer> eventTypeIdList;
	private List<String> eventSeverityList;
	private List<Integer> customAssetGroupIdList;
	private List<Integer> LandmarkCategoryList;
	private List<Integer> LandmarkIdList;
	private boolean ownStock;
	private boolean activeAlerts;	
	
	public List<Integer> getLoginTenancyIdList() {
		return loginTenancyIdList;
	}
	public void setLoginTenancyIdList(List<Integer> loginTenancyIdList) {
		this.loginTenancyIdList = loginTenancyIdList;
	}
	public boolean isOwnStock() {
		return ownStock;
	}
	public void setOwnStock(boolean ownStock) {
		this.ownStock = ownStock;
	}
	public List<Integer> getLandmarkIdList() {
		return LandmarkIdList;
	}
	public void setLandmarkIdList(List<Integer> landmarkIdList) {
		LandmarkIdList = landmarkIdList;
	}
	private boolean flag;
	public String getLoginId() {
		return LoginId;
	}
	public void setLoginId(String loginId) {
		LoginId = loginId;
	}
	public String getPeriod() {
		return Period;
	}
	public void setPeriod(String period) {
		Period = period;
	}
	public List<Integer> getTenancyIdList() {
		return TenancyIdList;
	}
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		TenancyIdList = tenancyIdList;
	}
	public List<Integer> getAssetGroupIdList() {
		return AssetGroupIdList;
	}
	public void setAssetGroupIdList(List<Integer> assetGroupIdList) {
		AssetGroupIdList = assetGroupIdList;
	}
	public List<Integer> getAssetTypeIdList() {
		return AssetTypeIdList;
	}
	public void setAssetTypeIdList(List<Integer> assetTypeIdList) {
		AssetTypeIdList = assetTypeIdList;
	}
	public List<Integer> getCustomAssetGroupIdList() {
		return customAssetGroupIdList;
	}
	public void setCustomAssetGroupIdList(List<Integer> customAssetGroupIdList) {
		this.customAssetGroupIdList = customAssetGroupIdList;
	}
	public List<Integer> getLandmarkCategoryList() {
		return LandmarkCategoryList;
	}
	public void setLandmarkCategoryList(List<Integer> landmarkCategoryList) {
		LandmarkCategoryList = landmarkCategoryList;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
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
	public boolean isActiveAlerts() {
		return activeAlerts;
	}
	public void setActiveAlerts(boolean activeAlerts) {
		this.activeAlerts = activeAlerts;
	}	
}
