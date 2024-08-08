package remote.wise.service.datacontract;

import java.util.List;

public class UnderUtilizedMachinesReqContract {
	
	private String loginID;
	private List<Integer> Tenancy_ID;
	private String Period;
	private List<Integer> MachineProfile_ID;
	private List<Integer> Model_ID;
	private List<Integer> MachineGroupType_ID;
	private List<Integer> CustomAssetGroup_ID;	
	
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	public List<Integer> getTenancy_ID() {
		return Tenancy_ID;
	}
	public void setTenancy_ID(List<Integer> tenancy_ID) {
		Tenancy_ID = tenancy_ID;
	}
	public String getPeriod() {
		return Period;
	}
	public void setPeriod(String period) {
		Period = period;
	}
	public List<Integer> getMachineProfile_ID() {
		return MachineProfile_ID;
	}
	public void setMachineProfile_ID(List<Integer> machineProfile_ID) {
		MachineProfile_ID = machineProfile_ID;
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
	public List<Integer> getCustomAssetGroup_ID() {
		return CustomAssetGroup_ID;
	}
	public void setCustomAssetGroup_ID(List<Integer> customAssetGroup_ID) {
		CustomAssetGroup_ID = customAssetGroup_ID;
	}
	

	
}
