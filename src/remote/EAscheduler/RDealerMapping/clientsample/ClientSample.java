package remote.EAscheduler.RDealerMapping.clientsample;

import remote.EAscheduler.RDealerMapping.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        RDealerMappingSampleService service1 = new RDealerMappingSampleService();
	        System.out.println("Create Web Service...");
	        RDealerMappingSample port1 = service1.getRDealerMappingSamplePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.reprocessEADealerMappingData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        RDealerMappingSample port2 = service1.getRDealerMappingSamplePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.reprocessEADealerMappingData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
