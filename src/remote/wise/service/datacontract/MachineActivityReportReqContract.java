package remote.wise.service.datacontract;

import java.util.List;

public class MachineActivityReportReqContract {
	

	//Custom Dates (fromDate,toDate) added by Juhi on 12-August-2013 
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
	
	
	private String Period;
	private List<Integer> MachineGroup_ID;
	private List<Integer> MachineProfile_ID;//assetgroup
	private List<Integer> Tenancy_ID;
	private List<Integer> Model_ID;//asset_type
	private List<Integer> MachineGroupType_ID;
	private String LoginId;
	private boolean isGroupingOnAssetGroup;
	//DefectID:1406 - Rajani Nagaraju - 20131028 - MachineGrouping issue in Reports and sending Report Totals information
	//Login Tenancy Id is required to identify whether the machine belongs to MG under user Login tenancy for returning MG details
	private List<Integer> loginTenancyIdList;
	private int minThreshold;
	private int maxThreshold;
	
	public boolean isGroupingOnAssetGroup() {
		return isGroupingOnAssetGroup;
	}
	public void setGroupingOnAssetGroup(boolean isGroupingOnAssetGroup) {
		this.isGroupingOnAssetGroup = isGroupingOnAssetGroup;
	}
	public String getPeriod() {
		return Period;
	}
	public void setPeriod(String period) {
		Period = period;
	}
	public List<Integer> getMachineGroup_ID() {
		return MachineGroup_ID;
	}
	public void setMachineGroup_ID(List<Integer> machineGroup_ID) {
		MachineGroup_ID = machineGroup_ID;
	}
	public List<Integer> getMachineProfile_ID() {
		return MachineProfile_ID;
	}
	public void setMachineProfile_ID(List<Integer> machineProfile_ID) {
		MachineProfile_ID = machineProfile_ID;
	}
	public List<Integer> getTenancy_ID() {
		return Tenancy_ID;
	}
	public void setTenancy_ID(List<Integer> tenancy_ID) {
		Tenancy_ID = tenancy_ID;
	}
	public List<Integer> getModel_ID() {
		return Model_ID;
	}
	public void setModel_ID(List<Integer> model_ID) {
		Model_ID = model_ID;
	}
	public List<Integer> getMachineGroupType_ID() {
		return MachineGroupType_ID;
	}
	public void setMachineGroupType_ID(List<Integer> machineGroupType_ID) {
		MachineGroupType_ID = machineGroupType_ID;
	}
	public String getLoginId() {
		return LoginId;
	}
	public void setLoginId(String loginId) {
		LoginId = loginId;
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
	 * @return the minThreshold
	 */
	public int getMinThreshold() {
		return minThreshold;
	}
	/**
	 * @param minThreshold the minThreshold to set
	 */
	public void setMinThreshold(int minThreshold) {
		this.minThreshold = minThreshold;
	}
	/**
	 * @return the maxThreshold
	 */
	public int getMaxThreshold() {
		return maxThreshold;
	}
	/**
	 * @param maxThreshold the maxThreshold to set
	 */
	public void setMaxThreshold(int maxThreshold) {
		this.maxThreshold = maxThreshold;
	}
	
	
}
