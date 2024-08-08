package remote.wise.service.datacontract;

public class ServiceDueOverDueRespContract {

	private int ServiceDueCount;
	private int ServiceOverDueCount;
	
	public int getServiceDueCount() {
		return ServiceDueCount;
	}
	public void setServiceDueCount(int serviceDueCount) {
		ServiceDueCount = serviceDueCount;
	}
	public int getServiceOverDueCount() {
		return ServiceOverDueCount;
	}
	public void setServiceOverDueCount(int serviceOverDueCount) {
		ServiceOverDueCount = serviceOverDueCount;
	}
	
}
