package remote.wise.client.CustomersUnderDealerService.clientsample;

import remote.wise.client.CustomersUnderDealerService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        CustomersUnderDealerServiceService service1 = new CustomersUnderDealerServiceService();
	        System.out.println("Create Web Service...");
	        CustomersUnderDealerService port1 = service1.getCustomersUnderDealerServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getCustomersForDealer(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        CustomersUnderDealerService port2 = service1.getCustomersUnderDealerServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getCustomersForDealer(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
