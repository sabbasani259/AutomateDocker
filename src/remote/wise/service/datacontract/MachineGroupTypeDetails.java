package remote.wise.service.datacontract;

import java.util.List;

public class MachineGroupTypeDetails {
	int machineGroupTypeId;
	String machineGroupTypeName;
	List<MachineGroupDetails> machineGroupList;
	/**
	 * @return the machineGroupTypeId
	 */
	public int getMachineGroupTypeId() {
		return machineGroupTypeId;
	}
	/**
	 * @param machineGroupTypeId the machineGroupTypeId to set
	 */
	public void setMachineGroupTypeId(int machineGroupTypeId) {
		this.machineGroupTypeId = machineGroupTypeId;
	}
	/**
	 * @return the machineGroupTypeName
	 */
	public String getMachineGroupTypeName() {
		return machineGroupTypeName;
	}
	/**
	 * @param machineGroupTypeName the machineGroupTypeName to set
	 */
	public void setMachineGroupTypeName(String machineGroupTypeName) {
		this.machineGroupTypeName = machineGroupTypeName;
	}
	/**
	 * @return the machineGroupList
	 */
	public List<MachineGroupDetails> getMachineGroupList() {
		return machineGroupList;
	}
	/**
	 * @param machineGroupList the machineGroupList to set
	 */
	public void setMachineGroupList(List<MachineGroupDetails> machineGroupList) {
		this.machineGroupList = machineGroupList;
	}

}
