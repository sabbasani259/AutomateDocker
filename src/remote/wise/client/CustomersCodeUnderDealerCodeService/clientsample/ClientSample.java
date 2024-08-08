package remote.wise.client.CustomersCodeUnderDealerCodeService.clientsample;

import remote.wise.client.CustomersCodeUnderDealerCodeService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault{
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        CustomersCodeUnderDealerCodeServiceService service1 = new CustomersCodeUnderDealerCodeServiceService();
	        System.out.println("Create Web Service...");
	        CustomersCodeUnderDealerCodeService port1 = service1.getCustomersCodeUnderDealerCodeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getCustomersCodeForDealer(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        CustomersCodeUnderDealerCodeService port2 = service1.getCustomersCodeUnderDealerCodeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getCustomersCodeForDealer(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
