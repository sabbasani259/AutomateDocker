package remote.wise.service.datacontract;

import java.util.List;

public class MapReqContract {
	private String LoginId;
	private List<String> SerialNumberList;
	private List<String> AlertSeverityList;
	private List<Integer> AlertTypeIdList;
	private List<Integer> Landmark_IdList;
	private List<Integer> LandmarkCategory_IdList;
	
	private List<Integer> Tenancy_ID;
	private List<Integer> loginUserTenancyList;
	private List<Integer> machineGroupIdList;
	private List<Integer>  machineProfileIdList;
	private List<Integer> modelIdList;
	private boolean ownStock;
	
	public List<Integer> getTenancy_ID() {
		return Tenancy_ID;
	}
	public void setTenancy_ID(List<Integer> tenancy_ID) {
		Tenancy_ID = tenancy_ID;
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
	public List<Integer> getLandmark_IdList() {
		return Landmark_IdList;
	}
	public void setLandmark_IdList(List<Integer> landmark_IdList) {
		Landmark_IdList = landmark_IdList;
	}
	public List<Integer> getLandmarkCategory_IdList() {
		return LandmarkCategory_IdList;
	}
	public void setLandmarkCategory_IdList(List<Integer> landmarkCategory_IdList) {
		LandmarkCategory_IdList = landmarkCategory_IdList;
	}
	public List<String> getAlertSeverityList() {
		return AlertSeverityList;
	}
	public void setAlertSeverityList(List<String> alertSeverityList) {
		AlertSeverityList = alertSeverityList;
	}
	public List<Integer> getAlertTypeIdList() {
		return AlertTypeIdList;
	}
	public void setAlertTypeIdList(List<Integer> alertTypeIdList) {
		AlertTypeIdList = alertTypeIdList;
	}
	public String getLoginId() {
		return LoginId;
	}
	public void setLoginId(String loginId) {
		LoginId = loginId;
	}
	public List<String> getSerialNumberList() {
		return SerialNumberList;
	}
	public void setSerialNumberList(List<String> serialNumberList) {
		SerialNumberList = serialNumberList;
	}
	public List<Integer> getLoginUserTenancyList() {
		return loginUserTenancyList;
	}
	public void setLoginUserTenancyList(List<Integer> loginUserTenancyList) {
		this.loginUserTenancyList = loginUserTenancyList;
	}
	public boolean isOwnStock() {
		return ownStock;
	}
	public void setOwnStock(boolean ownStock) {
		this.ownStock = ownStock;
	}
	
	
}
