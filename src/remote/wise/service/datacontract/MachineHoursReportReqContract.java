package remote.wise.service.datacontract;

import java.util.List;

public class MachineHoursReportReqContract {
	
	//Custom Dates (fromDate,toDate) added by Juhi on 13-August-2013 
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
	private List<Integer> MachineGroupIdList;//TABLE-machine_group_dimension
	private List<Integer> MachineProfileIdList;//Asset_Group_ID,TABLE-asset_class_dimension
	private List<Integer> TenancyIdList;//tenancy_dimension
	private List<Integer> ModelList;//Asset_Type_ID
	private boolean isGroupingOnAssetGroup;
	//private String LoginId;
	//DefectID:1406 - Rajani Nagaraju - 20131028 - MachineGrouping issue in Reports and sending Report Totals information
	//Login Tenancy Id is required to identify whether the machine belongs to MG under user Login tenancy for returning MG details
	private List<Integer> loginTenancyIdList;
	
	public String getPeriod() {
		return Period;
	}
	public void setPeriod(String period) {
		Period = period;
	}
	public List<Integer> getMachineGroupIdList() {
		return MachineGroupIdList;
	}
	public void setMachineGroupIdList(List<Integer> machineGroupIdList) {
		MachineGroupIdList = machineGroupIdList;
	}
	public List<Integer> getMachineProfileIdList() {
		return MachineProfileIdList;
	}
	public void setMachineProfileIdList(List<Integer> machineProfileIdList) {
		MachineProfileIdList = machineProfileIdList;
	}
	public List<Integer> getTenancyIdList() {
		return TenancyIdList;
	}
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		TenancyIdList = tenancyIdList;
	}
	public List<Integer> getModelList() {
		return ModelList;
	}
	public void setModelList(List<Integer> modelList) {
		ModelList = modelList;
	}
	/*public String getLoginId() {
		return LoginId;
	}
	public void setLoginId(String loginId) {
		LoginId = loginId;
	}
	*/
	public boolean isGroupingOnAssetGroup() {
		return isGroupingOnAssetGroup;
	}
	public void setGroupingOnAssetGroup(boolean isGroupingOnAssetGroup) {
		this.isGroupingOnAssetGroup = isGroupingOnAssetGroup;
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
	
	
}
