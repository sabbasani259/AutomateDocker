package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class UtilizationDetailReportReqContract 
{
	String loginId;
	String period;
	List<Integer> tenancyIdList;
	List<Integer> machineGroupIdList;
	List<Integer> machineProfileIdList;
	List<Integer> modelIdList;
	boolean isGroupingOnAssetGroup;
	List<String> serialNumList;
	
	public List<String> getSerialNumList() {
		return serialNumList;
	}
	public void setSerialNumList(List<String> serialNumList) {
		this.serialNumList = serialNumList;
	}
	/**
	 * @return user login Id
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId userLoginId as String input
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	
	/**
	 * @return period as String - either of Today,Week,Month,Quarter,Year
	 */
	public String getPeriod() {
		return period;
	}
	/**
	 * @param period Period as String input
	 */
	public void setPeriod(String period) {
		this.period = period;
	}
	
	
	/**
	 * @return the tenancyIdList
	 */
	public List<Integer> getTenancyIdList() {
		return tenancyIdList;
	}
	/**
	 * @param tenancyIdList the tenancyIdList to set as IntegerList input
	 */
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		this.tenancyIdList = tenancyIdList;
	}
	
	
	/**
	 * @return the machineGroupIdList
	 */
	public List<Integer> getMachineGroupIdList() {
		return machineGroupIdList;
	}
	/**
	 * @param machineGroupIdList the machineGroupIdList to set as IntegerList input
	 */
	public void setMachineGroupIdList(List<Integer> machineGroupIdList) {
		this.machineGroupIdList = machineGroupIdList;
	}
	
	
	/**
	 * @return the machineProfileIdList
	 */
	public List<Integer> getMachineProfileIdList() {
		return machineProfileIdList;
	}
	/**
	 * @param machineProfileIdList the machineProfileIdList to set as IntegerList input
	 */
	public void setMachineProfileIdList(List<Integer> machineProfileIdList) {
		this.machineProfileIdList = machineProfileIdList;
	}
	
	
	/**
	 * @return the modelIdList
	 */
	public List<Integer> getModelIdList() {
		return modelIdList;
	}
	/**
	 * @param modelIdList the modelIdList to set as IntegerList input
	 */
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
