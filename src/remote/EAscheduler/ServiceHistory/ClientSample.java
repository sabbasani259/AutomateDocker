package remote.EAscheduler.ServiceHistory;

public class ClientSample {

	public static void main(String[] args) {
	        ServiceHistorySampleService service1 = new ServiceHistorySampleService();
	        ServiceHistorySample port1 = service1.getServiceHistorySamplePort();
	        port1.processEAServiceHistory("ServiceHistory");
	}
}
