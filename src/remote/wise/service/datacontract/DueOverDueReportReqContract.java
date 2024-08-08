package remote.wise.service.datacontract;

import java.util.List;

/**
 * @author Rajani Nagaraju
 *
 */
public class DueOverDueReportReqContract 
{
	String loginId;
	List<Integer> tenancyIdList;
	List<Integer> machineGroupIdList;
	List<Integer> machineProfileIdList;
	List<Integer> modelIdList;
	boolean isOverdueReport;
	
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
	/**
	 * @return the tenancyIdList
	 */
	public List<Integer> getTenancyIdList() {
		return tenancyIdList;
	}
	/**
	 * @param tenancyIdList the tenancyIdList to set
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
	 * @param machineGroupIdList the machineGroupIdList to set
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
	 * @param machineProfileIdList the machineProfileIdList to set
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
	 * @param modelIdList the modelIdList to set
	 */
	public void setModelIdList(List<Integer> modelIdList) {
		this.modelIdList = modelIdList;
	}
	/**
	 * @return the isOverdueReport
	 */
	public boolean isOverdueReport() {
		return isOverdueReport;
	}
	/**
	 * @param isOverdueReport the isOverdueReport to set
	 */
	public void setOverdueReport(boolean isOverdueReport) {
		this.isOverdueReport = isOverdueReport;
	}
	
	
}
