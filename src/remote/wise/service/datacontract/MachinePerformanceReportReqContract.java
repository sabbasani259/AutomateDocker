package remote.wise.service.datacontract;

import java.util.List;

public class MachinePerformanceReportReqContract {
	
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
	private String period;
	List<Integer> machineGroupIdList;
	List<Integer> machineProfileIdList;////AssetGroupIdList,TABLE-asset_class_dimension
	List<Integer> tenancyIdList;
	List<Integer> modelIdList;//AssetTypeIdList,TABLE-asset_class_dimension
	private boolean isGroupingOnAssetGroup;
	
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
	public List<Integer> getTenancyIdList() {
		return tenancyIdList;
	}
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		this.tenancyIdList = tenancyIdList;
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

}
