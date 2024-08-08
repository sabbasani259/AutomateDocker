/**

 * 
 */
package remote.wise.service.datacontract;

import java.util.List;

// CR316 : Prafull  : 2022-05-10 : Block Notifications for Tamper Alert _ EFD 


/**
 * @author sunayak
 *
 */
public class AlertSeverityReportReqContract {
    private String LoginId;
    private String Period;
	private List<Integer> loginTenancyIdList;
	private List<Integer> TenancyIdList;
	private List<Integer> AssetGroupIdList; 
	private List<Integer> AssetTypeIdList;
	private List<Integer> eventTypeIdList;
	private List<Integer> blockedEventIdList; // CR316_20220510.n Prafull
	private List<String> eventSeverityList;
	private List<Integer> customAssetGroupIdList;
	private List<Integer> LandmarkCategoryList;
	private List<Integer> LandmarkIdList;
	private boolean ownStock;
	private boolean activeAlerts;	
	private boolean flag;
	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return LoginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		LoginId = loginId;
	}
	/**
	 * @return the period
	 */
	public String getPeriod() {
		return Period;
	}
	/**
	 * @param period the period to set
	 */
	public void setPeriod(String period) {
		Period = period;
	}
	/**
	 * @return the loginTenancyIdList
	 */
	public List<Integer> getLoginTenancyIdList() {
		return loginTenancyIdList;
	}
	/**
	 * @param loginTenancyIdList the loginTenancyIdList to set
	 */
	public void setLoginTenancyIdList(List<Integer> loginTenancyIdList) {
		this.loginTenancyIdList = loginTenancyIdList;
	}
	/**
	 * @return the tenancyIdList
	 */
	public List<Integer> getTenancyIdList() {
		return TenancyIdList;
	}
	/**
	 * @param tenancyIdList the tenancyIdList to set
	 */
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		TenancyIdList = tenancyIdList;
	}
	/**
	 * @return the assetGroupIdList
	 */
	public List<Integer> getAssetGroupIdList() {
		return AssetGroupIdList;
	}
	/**
	 * @param assetGroupIdList the assetGroupIdList to set
	 */
	public void setAssetGroupIdList(List<Integer> assetGroupIdList) {
		AssetGroupIdList = assetGroupIdList;
	}
	/**
	 * @return the assetTypeIdList
	 */
	public List<Integer> getAssetTypeIdList() {
		return AssetTypeIdList;
	}
	/**
	 * @param assetTypeIdList the assetTypeIdList to set
	 */
	public void setAssetTypeIdList(List<Integer> assetTypeIdList) {
		AssetTypeIdList = assetTypeIdList;
	}
	/**
	 * @return the eventTypeIdList
	 */
	public List<Integer> getEventTypeIdList() {
		return eventTypeIdList;
	}
	/**
	 * @param eventTypeIdList the eventTypeIdList to set
	 */
	public void setEventTypeIdList(List<Integer> eventTypeIdList) {
		this.eventTypeIdList = eventTypeIdList;
	}	
	/**
	 * @return the blockedEventIdList
	 */
	public List<Integer> getBlockedEventIdList() {
		return blockedEventIdList;
	}
	/**
	 * @param blockedEventIdList the blockedEventIdList to set
	 */
	public void setBlockedEventIdList(List<Integer> blockedEventIdList) {
		this.blockedEventIdList = blockedEventIdList;
	}
	/**
	 * @return the eventSeverityList
	 */
	public List<String> getEventSeverityList() {
		return eventSeverityList;
	}
	/**
	 * @param eventSeverityList the eventSeverityList to set
	 */
	public void setEventSeverityList(List<String> eventSeverityList) {
		this.eventSeverityList = eventSeverityList;
	}
	/**
	 * @return the customAssetGroupIdList
	 */
	public List<Integer> getCustomAssetGroupIdList() {
		return customAssetGroupIdList;
	}
	/**
	 * @param customAssetGroupIdList the customAssetGroupIdList to set
	 */
	public void setCustomAssetGroupIdList(List<Integer> customAssetGroupIdList) {
		this.customAssetGroupIdList = customAssetGroupIdList;
	}
	/**
	 * @return the landmarkCategoryList
	 */
	public List<Integer> getLandmarkCategoryList() {
		return LandmarkCategoryList;
	}
	/**
	 * @param landmarkCategoryList the landmarkCategoryList to set
	 */
	public void setLandmarkCategoryList(List<Integer> landmarkCategoryList) {
		LandmarkCategoryList = landmarkCategoryList;
	}
	/**
	 * @return the landmarkIdList
	 */
	public List<Integer> getLandmarkIdList() {
		return LandmarkIdList;
	}
	/**
	 * @param landmarkIdList the landmarkIdList to set
	 */
	public void setLandmarkIdList(List<Integer> landmarkIdList) {
		LandmarkIdList = landmarkIdList;
	}
	/**
	 * @return the ownStock
	 */
	public boolean isOwnStock() {
		return ownStock;
	}
	/**
	 * @param ownStock the ownStock to set
	 */
	public void setOwnStock(boolean ownStock) {
		this.ownStock = ownStock;
	}
	/**
	 * @return the activeAlerts
	 */
	public boolean isActiveAlerts() {
		return activeAlerts;
	}
	/**
	 * @param activeAlerts the activeAlerts to set
	 */
	public void setActiveAlerts(boolean activeAlerts) {
		this.activeAlerts = activeAlerts;
	}
	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}
	/**
	 * @param flag the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}
