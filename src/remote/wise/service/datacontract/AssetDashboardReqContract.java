package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class AssetDashboardReqContract 
{
	String loginId;
	List<Integer> userTenancyIdList;
	List<Integer> tenancyIdList;
	String serialNumber;
	List<Integer> machineProfileIdList;
	List<Integer> modelList;
	List<Integer> machineGroupTypeIdList;
	List<Integer> machineGroupIdList;
	List<String> alertSeverityList;
	List<Integer> alertTypeIdList;
	int pageNumber;
	List<Integer> landmarkCategoryIdList;
	List<Integer> landmarkIdList;
	boolean ownStock;
//	DefectId:20150706 @Suprava Adding Mobile Number as New Search criteria in Fleet General Tab.
	String mobilenumber;
	

	public AssetDashboardReqContract()
	{
		loginId=null;
		userTenancyIdList=null;
		tenancyIdList=null;
		serialNumber=null;
		machineProfileIdList=null;
		modelList=null;
		machineGroupTypeIdList=null;
		machineGroupIdList=null;
		alertSeverityList=null;
		alertTypeIdList=null;
		alertTypeIdList=null;
		pageNumber=0;
		landmarkCategoryIdList = null;
		landmarkIdList = null;
		mobilenumber = null;
	}

	/**
	 * @return the user loginId
	 */
	public String getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId the User loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	/**
	 * @return the logged in user TenancyIdList 
	 */
	public List<Integer> getUserTenancyIdList() {
		return userTenancyIdList;
	}

	/**
	 * @param userTenancyIdList the login user TenancyIdList to set
	 */
	public void setUserTenancyIdList(List<Integer> userTenancyIdList) {
		this.userTenancyIdList = userTenancyIdList;
	}

	/**
	 * @return the tenancyIdList list of childTenancies
	 */
	public List<Integer> getTenancyIdList() {
		return tenancyIdList;
	}

	/**
	 * @param tenancyIdList the List of child tenancies to set
	 */
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		this.tenancyIdList = tenancyIdList;
	}

	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber the VIN to set as String input
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * @return the machineProfileIdList
	 */
	public List<Integer> getMachineProfileIdList() {
		return machineProfileIdList;
	}

	/**
	 * @param machineProfileIdList the machineProfileIdList to set
	 */
	public void setMachineProfileIdList(List<Integer> machineProfileIdList) {
		this.machineProfileIdList = machineProfileIdList;
	}

	/**
	 * @return the modelList
	 */
	public List<Integer> getModelList() {
		return modelList;
	}

	/**
	 * @param modelList the modelList to set
	 */
	public void setModelList(List<Integer> modelList) {
		this.modelList = modelList;
	}

	/**
	 * @return the machineGroupTypeIdList
	 */
	public List<Integer> getMachineGroupTypeIdList() {
		return machineGroupTypeIdList;
	}

	/**
	 * @param machineGroupTypeIdList the custom machineGroupTypeIdList to set
	 */
	public void setMachineGroupTypeIdList(List<Integer> machineGroupTypeIdList) {
		this.machineGroupTypeIdList = machineGroupTypeIdList;
	}

	/**
	 * @return the machineGroupIdList
	 */
	public List<Integer> getMachineGroupIdList() {
		return machineGroupIdList;
	}

	/**
	 * @param machineGroupIdList the custom machineGroupIdList to set
	 */
	public void setMachineGroupIdList(List<Integer> machineGroupIdList) {
		this.machineGroupIdList = machineGroupIdList;
	}

	/**
	 * @return the alertSeverityList
	 */
	public List<String> getAlertSeverityList() {
		return alertSeverityList;
	}

	/**
	 * @param alertSeverityList the alertSeverityList to set
	 */
	public void setAlertSeverityList(List<String> alertSeverityList) {
		this.alertSeverityList = alertSeverityList;
	}

	/**
	 * @return the alertTypeIdList
	 */
	public List<Integer> getAlertTypeIdList() {
		return alertTypeIdList;
	}

	/**
	 * @param alertTypeIdList the alertTypeIdList to set
	 */
	public void setAlertTypeIdList(List<Integer> alertTypeIdList) {
		this.alertTypeIdList = alertTypeIdList;
	}

	/**
	 * @return the pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * @return the landmarkCategoryIdList
	 */
	public List<Integer> getLandmarkCategoryIdList() {
		return landmarkCategoryIdList;
	}

	/**
	 * @param landmarkCategoryIdList the landmarkCategoryIdList to set
	 */
	public void setLandmarkCategoryIdList(List<Integer> landmarkCategoryIdList) {
		this.landmarkCategoryIdList = landmarkCategoryIdList;
	}

	/**
	 * @return the landmarkIdList
	 */
	public List<Integer> getLandmarkIdList() {
		return landmarkIdList;
	}

	/**
	 * @param landmarkIdList the landmarkIdList to set
	 */
	public void setLandmarkIdList(List<Integer> landmarkIdList) {
		this.landmarkIdList = landmarkIdList;
	}

	public boolean isOwnStock() {
		return ownStock;
	}

	public void setOwnStock(boolean ownStock) {
		this.ownStock = ownStock;
	}
	/**
	 * @return the mobilenumber
	 */
	public String getMobilenumber() {
		return mobilenumber;
	}

	/**
	 * @param mobilenumber the mobilenumber to set
	 */
	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}	
			
}
