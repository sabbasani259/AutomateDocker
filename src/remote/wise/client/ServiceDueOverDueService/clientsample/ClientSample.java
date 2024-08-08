package remote.wise.client.ServiceDueOverDueService.clientsample;

import remote.wise.client.ServiceDueOverDueService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ServiceDueOverDueServiceService service1 = new ServiceDueOverDueServiceService();
	        System.out.println("Create Web Service...");
	        ServiceDueOverDueService port1 = service1.getServiceDueOverDueServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getServiceDueOverDueDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ServiceDueOverDueService port2 = service1.getServiceDueOverDueServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getServiceDueOverDueDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
