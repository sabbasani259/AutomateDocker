package remote.wise.service.datacontract;

import java.util.List;

public class NotificationSummaryReportReqContract {

	
	
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
	private String Period;
	private String LoginId;
	private List<Integer> TenancyIdList;
	private List<Integer> ModelidList;
	//Defect Id:1406 Added loginTenancyIdList by Juhi on 30-October-2013
	List<Integer> loginTenancyIdList;
	
	public List<Integer> getLoginTenancyIdList() {
		return loginTenancyIdList;
	}
	public void setLoginTenancyIdList(List<Integer> loginTenancyIdList) {
		this.loginTenancyIdList = loginTenancyIdList;
	}
	//added by smitha on july 24th...Defect id 800 and 1041
	private List<Integer> alertTypeIdList;
	
	public List<Integer> getAlertTypeIdList() {
		return alertTypeIdList;
	}
	public void setAlertTypeIdList(List<Integer> alertTypeIdList) {
		this.alertTypeIdList = alertTypeIdList;
	}
	//ended on july 24th.....Defect id 800 and 1041
	
	public List<Integer> getModelidList() {
		return ModelidList;
	}
	public void setModelidList(List<Integer> modelidList) {
		ModelidList = modelidList;
	}
	public List<Integer> getTenancyIdList() {
		return TenancyIdList;
	}
	public List<Integer> getMachineGroupIdList() {
		return MachineGroupIdList;
	}
	public List<Integer> getMachineProfileIdList() {
		return MachineProfileIdList;
	}
	private List<Integer> MachineGroupIdList;
	private List<Integer> MachineProfileIdList;
	
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		TenancyIdList = tenancyIdList;
	}
	public void setMachineGroupIdList(List<Integer> machineGroupIdList) {
		MachineGroupIdList = machineGroupIdList;
	}
	public void setMachineProfileIdList(List<Integer> machineProfileIdList) {
		MachineProfileIdList = machineProfileIdList;
	}
	private String ModelList;
	
	private boolean machineGroup, machineProfile, model;
	
	public boolean isMachineGroup() {
		return machineGroup;
	}
	public void setMachineGroup(boolean machineGroup) {
		this.machineGroup = machineGroup;
	}
	public boolean isMachineProfile() {
		return machineProfile;
	}
	public void setMachineProfile(boolean machineProfile) {
		this.machineProfile = machineProfile;
	}
	public boolean isModel() {
		return model;
	}
	public void setModel(boolean model) {
		this.model = model;
	}
	public String getPeriod() {
		return Period;
	}
	public void setPeriod(String period) {
		Period = period;
	}

	public String getModelList() {
		return ModelList;
	}
	public void setModelList(String modelList) {
		ModelList = modelList;
	}
	public String getLoginId() {
		return LoginId;
	}
	public void setLoginId(String loginId) {
		LoginId = loginId;
	}

	
}
