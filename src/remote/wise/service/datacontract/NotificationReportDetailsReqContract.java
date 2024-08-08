package remote.wise.service.datacontract;

import java.util.List;

public class NotificationReportDetailsReqContract {
	//to get the individual cont of alerts for each VIN...ID20131022..done by smitha on 22nd oct 2013	
	private List<Integer> TenancyIdList;
//	Keerthi : 13/01/14 : added for landmark details
	private List<Integer> loginTenancyIdList;
	private List<Integer> alertTypeIdList;
//Suprava :2015-05-13 : added filter criteria
	private List<Integer> ModelidList;
	private List<Integer> MachineGroupIdList;
	private List<Integer> MachineProfileIdList;
	/**
	 * @return the modelidList
	 */
	public List<Integer> getModelidList() {
		return ModelidList;
	}
	/**
	 * @param modelidList the modelidList to set
	 */
	public void setModelidList(List<Integer> modelidList) {
		ModelidList = modelidList;
	}
	/**
	 * @return the machineGroupIdList
	 */
	public List<Integer> getMachineGroupIdList() {
		return MachineGroupIdList;
	}
	/**
	 * @param machineGroupIdList the machineGroupIdList to set
	 */
	public void setMachineGroupIdList(List<Integer> machineGroupIdList) {
		MachineGroupIdList = machineGroupIdList;
	}
	/**
	 * @return the machineProfileIdList
	 */
	public List<Integer> getMachineProfileIdList() {
		return MachineProfileIdList;
	}
	/**
	 * @param machineProfileIdList the machineProfileIdList to set
	 */
	public void setMachineProfileIdList(List<Integer> machineProfileIdList) {
		MachineProfileIdList = machineProfileIdList;
	}
	public List<Integer> getLoginTenancyIdList() {
		return loginTenancyIdList;
	}
	public void setLoginTenancyIdList(List<Integer> loginTenancyIdList) {
		this.loginTenancyIdList = loginTenancyIdList;
	}
	public List<Integer> getTenancyIdList() {
		return TenancyIdList;
	}
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		TenancyIdList = tenancyIdList;
	}
	//ended on 22nd oct 2013...ID20131022
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
	//added by smitha....DefectID-20131024
    private List<String> serialNumberList;

	public List<String> getSerialNumberList() {
		return serialNumberList;
	}
	public void setSerialNumberList(List<String> serialNumberList) {
		this.serialNumberList = serialNumberList;
	}
	public List<Integer> getAlertTypeIdList() {
		return alertTypeIdList;
	}
	public void setAlertTypeIdList(List<Integer> alertTypeIdList) {
		this.alertTypeIdList = alertTypeIdList;
	}
	
}
