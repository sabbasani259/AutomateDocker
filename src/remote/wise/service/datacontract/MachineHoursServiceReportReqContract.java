package remote.wise.service.datacontract;

import java.util.List;

public class MachineHoursServiceReportReqContract {
	
	//Custom Dates (fromDate,toDate) added by Juhi on 13-August-2013 
/*	private String fromDate;
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
	}*/
//	private String Period;
	private String loginId;     // DefectID 20160318 @Kishore code changes for sending report over mail to the login id
	private List<Integer> machineGroupIdList;//TABLE-machine_group_dimension (CustomAssetGroupIdList) 
	private List<Integer> machineProfileIdList; //AssetGroupIdList,TABLE-asset_class_dimension
	private List<Integer> tenancyIdList;
	private List<Integer> modelIdList;//AssetTypeIdList,TABLE-asset_class_dimension
	private boolean isGroupingOnAssetGroup;
	
//Defect Id:1406 Added loginTenancyIdList by Juhi on 29-October-2013
	List<Integer> loginTenancyIdList;
	
	public List<Integer> getLoginTenancyIdList() {
		return loginTenancyIdList;
	}
	public void setLoginTenancyIdList(List<Integer> loginTenancyIdList) {
		this.loginTenancyIdList = loginTenancyIdList;
	}
	/*public String getPeriod() {
		return Period;
	}
	public void setPeriod(String period) {
		Period = period;
	}*/
	public List<Integer> getTenancyIdList() {
		return tenancyIdList;
	}
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		this.tenancyIdList = tenancyIdList;
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
	public List<Integer> getModelIdList() {
		return modelIdList;
	}
	public void setModelIdList(List<Integer> modelIdList) {
		this.modelIdList = modelIdList;
	}
	public boolean isGroupingOnAssetGroup() {
		return isGroupingOnAssetGroup;
	}
	public void setGroupingOnAssetGroup(boolean isGroupingOnAssetGroup) {
		this.isGroupingOnAssetGroup = isGroupingOnAssetGroup;
	}
	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	
	

	
 

}
