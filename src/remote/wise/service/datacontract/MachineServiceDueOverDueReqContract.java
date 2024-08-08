package remote.wise.service.datacontract;

import java.util.List;

public class MachineServiceDueOverDueReqContract {
	
	//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	List<Integer> loggedInTenancyId;
	//String loggedInTenancyId;
	String period;
	////added by smitha on oct 14th 2013[grouping in summary report Internal Defect 20131014 request contract change]
	List<Integer> machineGroupIdList;
	List<Integer> machineProfileIdList;
	List<Integer> modelIdList;
	
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
	//ended on oct 14th 2013[grouping in summary report Internal Defect 20131014 request contract change]
	/**
	 * @return the period
	 */
	public String getPeriod() {
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(String period) {
		this.period = period;
	}

	//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	public List<Integer> getLoggedInTenancyId() {
		return loggedInTenancyId;
	}

	public void setLoggedInTenancyId(List<Integer> loggedInTenancyId) {
		this.loggedInTenancyId = loggedInTenancyId;
	}

	/**
	 * @return the loggedInTenancyId
	 *//*
	public String getLoggedInTenancyId() {
		return loggedInTenancyId;
	}

	*//**
	 * @param loggedInTenancyId the loggedInTenancyId to set
	 *//*
	public void setLoggedInTenancyId(String loggedInTenancyId) {
		this.loggedInTenancyId = loggedInTenancyId;
	}*/
	
	
	
}
