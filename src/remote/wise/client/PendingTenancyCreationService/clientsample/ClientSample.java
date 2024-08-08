package remote.wise.client.PendingTenancyCreationService.clientsample;

import remote.wise.client.PendingTenancyCreationService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        PendingTenancyCreationServiceService service1 = new PendingTenancyCreationServiceService();
	        System.out.println("Create Web Service...");
	        PendingTenancyCreationService port1 = service1.getPendingTenancyCreationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getPendingTenancyCreation(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        PendingTenancyCreationService port2 = service1.getPendingTenancyCreationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getPendingTenancyCreation(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
