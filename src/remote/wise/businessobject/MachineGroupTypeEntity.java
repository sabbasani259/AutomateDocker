/**
 * 
 */
package remote.wise.businessobject;

import java.util.List;

/**
 * @author kprabhu5
 *
 */
public class MachineGroupTypeEntity {
	int machineGroupTypeId;
	String machineGroupTypeName;
	List<MachineGroupEntity> machineGroupList;
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
	public List<MachineGroupEntity> getMachineGroupList() {
		return machineGroupList;
	}
	/**
	 * @param machineGroupList the machineGroupList to set
	 */
	public void setMachineGroupList(List<MachineGroupEntity> machineGroupList) {
		this.machineGroupList = machineGroupList;
	}

}
