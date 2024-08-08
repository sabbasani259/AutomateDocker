package remote.wise.EAintegration.clientPackage.DealerMappingService.clientsample;

import remote.wise.EAintegration.clientPackage.DealerMappingService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DealerMappingServiceService service1 = new DealerMappingServiceService();
	        System.out.println("Create Web Service...");
	        DealerMappingService port1 = service1.getDealerMappingServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setDealerMapping(null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DealerMappingService port2 = service1.getDealerMappingServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setDealerMapping(null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
