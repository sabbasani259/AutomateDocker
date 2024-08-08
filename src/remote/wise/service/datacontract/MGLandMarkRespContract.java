package remote.wise.service.datacontract;

import java.util.List;

public class MGLandMarkRespContract {	
	
	List<LandmarkCategory> landmarkCategoryList;
	List<MachineGroupTypeDetails> machineGroupTypeList;
	
	/**
	 * @return the landmarkCategoryList
	 */
	public List<LandmarkCategory> getLandmarkCategoryList() {
		return landmarkCategoryList;
	}
	/**
	 * @param landmarkCategoryList the landmarkCategoryList to set
	 */
	public void setLandmarkCategoryList(List<LandmarkCategory> landmarkCategoryList) {
		this.landmarkCategoryList = landmarkCategoryList;
	}
	/**
	 * @return the machineGroupTypeList
	 */
	public List<MachineGroupTypeDetails> getMachineGroupTypeList() {
		return machineGroupTypeList;
	}
	/**
	 * @param machineGroupTypeList the machineGroupTypeList to set
	 */
	public void setMachineGroupTypeList(
			List<MachineGroupTypeDetails> machineGroupTypeList) {
		this.machineGroupTypeList = machineGroupTypeList;
	}
}
