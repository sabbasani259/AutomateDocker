package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class UtilizationSummaryReportReqContract 
{
	//Custom Dates (fromDate,toDate) added by Juhi on 14-August-2013 
	private String fromDate;
	private String toDate;	
	
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	String loginId;
	String period;
	List<Integer> tenancyIdList;
	List<Integer> machineGroupIdList;
	List<Integer> assetGroupIdList;
	List<Integer> customAssetGroupIdList;
//	List<Integer> machineProfileIdList;
	List<Integer> modelIdList;
	private boolean isGroupingOnAssetGroup;
	List<Integer> loginTenancyIdList;
		
	/**
	 * @return user login Id
	 */
	public String getLoginId() {
		return loginId;
	}
	public List<Integer> getLoginTenancyIdList() {
		return loginTenancyIdList;
	}
	public void setLoginTenancyIdList(List<Integer> loginTenancyIdList) {
		this.loginTenancyIdList = loginTenancyIdList;
	}
	/**
	 * @param loginId userLoginId as String input
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	
	/**
	 * @return period as String - either of Today,Week,Month,Quarter,Year
	 */
	public String getPeriod() {
		return period;
	}
	/**
	 * @param period Period as String input
	 */
	public void setPeriod(String period) {
		this.period = period;
	}
	
	
	/**
	 * @return the tenancyIdList
	 */
	public List<Integer> getTenancyIdList() {
		return tenancyIdList;
	}
	/**
	 * @param tenancyIdList the tenancyIdList to set as IntegerList input
	 */
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		this.tenancyIdList = tenancyIdList;
	}
	
	
	/**
	 * @return the machineGroupIdList
	 */
	public List<Integer> getMachineGroupIdList() {
		return machineGroupIdList;
	}
	/**
	 * @param machineGroupIdList the machineGroupIdList to set as IntegerList input
	 */
	public void setMachineGroupIdList(List<Integer> machineGroupIdList) {
		this.machineGroupIdList = machineGroupIdList;
	}
	
	
	/**
	 * @return the machineProfileIdList
	 */
	/*public List<Integer> getMachineProfileIdList() {
		return machineProfileIdList;
	}
	*//**
	 * @param machineProfileIdList the machineProfileIdList to set as IntegerList input
	 *//*
	public void setMachineProfileIdList(List<Integer> machineProfileIdList) {
		this.machineProfileIdList = machineProfileIdList;
	}*/
	public List<Integer> getAssetGroupIdList() {
		return assetGroupIdList;
	}
	public void setAssetGroupIdList(List<Integer> assetGroupIdList) {
		this.assetGroupIdList = assetGroupIdList;
	}
	
	/**
	 * @return the modelIdList
	 */
	public List<Integer> getModelIdList() {
		return modelIdList;
	}
	
	/**
	 * @param modelIdList the modelIdList to set as IntegerList input
	 */
	public void setModelIdList(List<Integer> modelIdList) {
		this.modelIdList = modelIdList;
	}
	public List<Integer> getCustomAssetGroupIdList() {
		return customAssetGroupIdList;
	}
	public void setCustomAssetGroupIdList(List<Integer> customAssetGroupIdList) {
		this.customAssetGroupIdList = customAssetGroupIdList;
	}
	public boolean isGroupingOnAssetGroup() {
		return isGroupingOnAssetGroup;
	}
	public void setGroupingOnAssetGroup(boolean isGroupingOnAssetGroup) {
		this.isGroupingOnAssetGroup = isGroupingOnAssetGroup;
	}
	
	
}
