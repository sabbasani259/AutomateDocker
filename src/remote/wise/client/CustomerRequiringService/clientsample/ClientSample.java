package remote.wise.client.CustomerRequiringService.clientsample;

import remote.wise.client.CustomerRequiringService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        CustomerRequiringServiceService service1 = new CustomerRequiringServiceService();
	        System.out.println("Create Web Service...");
	        CustomerRequiringService port1 = service1.getCustomerRequiringServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getCustomerRequiring(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        CustomerRequiringService port2 = service1.getCustomerRequiringServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getCustomerRequiring(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
