package remote.wise.service.datacontract;

import java.util.List;

public class FleetSummaryReportReqContract {
	
	//Custom Dates (fromDate,toDate) added by Juhi on 19-August-2013 
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
	
	private String period;
	private List<Integer> machineGroupIdList;//TABLE-machine_group_dimension
	private List<Integer> machineProfileIdList;//Asset_Group_ID,TABLE-asset_class_dimension
	private List<Integer> tenanctIdList;//tenancy_dimension
	private List<Integer> modelIdList;//Asset_Type_ID
	private boolean isGroupingOnAssetGroup;
	/*private List<Integer> landmarkIdList;
	private List<Integer> landmarkCategoryIdList;*/
	//added by smitha on oct 29th 2013 Defect ID 20131029....Reports summary header tab
	private List<Integer> loginTenancyIdList ;	
	
	public List<Integer> getLoginTenancyIdList() {
		return loginTenancyIdList;
	}
	public void setLoginTenancyIdList(List<Integer> loginTenancyIdList) {
		this.loginTenancyIdList = loginTenancyIdList;
	}
	//ended on oct 29th 2013 Defect ID 20131029.......Reports summary header tab
	public boolean isGroupingOnAssetGroup() {
		return isGroupingOnAssetGroup;
	}
	public void setGroupingOnAssetGroup(boolean isGroupingOnAssetGroup) {
		this.isGroupingOnAssetGroup = isGroupingOnAssetGroup;
	}
	
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public List<Integer> getMachineGroupIdList() {
		return machineGroupIdList;
	}
	public void setMachineGroupIdList(List<Integer> machineGroupIdList) {
		this.machineGroupIdList = machineGroupIdList;
	}
	public List<Integer> getMachineProfileIdList() {
		return machineProfileIdList;
	}
	public void setMachineProfileIdList(List<Integer> machineProfileIdList) {
		this.machineProfileIdList = machineProfileIdList;
	}
	public List<Integer> getTenanctIdList() {
		return tenanctIdList;
	}
	public void setTenanctIdList(List<Integer> tenanctIdList) {
		this.tenanctIdList = tenanctIdList;
	}
	public List<Integer> getModelIdList() {
		return modelIdList;
	}
	public void setModelIdList(List<Integer> modelIdList) {
		this.modelIdList = modelIdList;
	}
	/*public List<Integer> getLandmarkIdList() {
		return landmarkIdList;
	}
	public void setLandmarkIdList(List<Integer> landmarkIdList) {
		this.landmarkIdList = landmarkIdList;
	}
	public List<Integer> getLandmarkCategoryIdList() {
		return landmarkCategoryIdList;
	}
	public void setLandmarkCategoryIdList(List<Integer> landmarkCategoryIdList) {
		this.landmarkCategoryIdList = landmarkCategoryIdList;
	}*/
	
	
	
}
