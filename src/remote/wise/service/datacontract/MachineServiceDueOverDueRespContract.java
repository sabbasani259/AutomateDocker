package remote.wise.service.datacontract;

public class MachineServiceDueOverDueRespContract {
	
	private String dealerTenancyId;
	private String dealerName;
	private long machineCountDueForService;
	private long machineCountOverdueForService;
	
	/**
	 * @return the dealerTenancyId
	 */
	public String getDealerTenancyId() {
		return dealerTenancyId;
	}
	/**
	 * @param dealerTenancyId the dealerTenancyId to set
	 */
	public void setDealerTenancyId(String dealerTenancyId) {
		this.dealerTenancyId = dealerTenancyId;
	}
	/**
	 * @return the dealerName
	 */
	public String getDealerName() {
		return dealerName;
	}
	/**
	 * @param dealerName the dealerName to set
	 */
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	/**
	 * @return the machineCountDueForService
	 */
	public long getMachineCountDueForService() {
		return machineCountDueForService;
	}
	/**
	 * @param machineCountDueForService the machineCountDueForService to set
	 */
	public void setMachineCountDueForService(long machineCountDueForService) {
		this.machineCountDueForService = machineCountDueForService;
	}
	/**
	 * @return the machineCountOverdueForService
	 */
	public long getMachineCountOverdueForService() {
		return machineCountOverdueForService;
	}
	/**
	 * @param machineCountOverdueForService the machineCountOverdueForService to set
	 */
	public void setMachineCountOverdueForService(long machineCountOverdueForService) {
		this.machineCountOverdueForService = machineCountOverdueForService;
	}
}
