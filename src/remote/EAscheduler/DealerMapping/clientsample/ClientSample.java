package remote.EAscheduler.DealerMapping.clientsample;

import remote.EAscheduler.DealerMapping.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DealerMappingSampleService service1 = new DealerMappingSampleService();
	        System.out.println("Create Web Service...");
	        DealerMappingSample port1 = service1.getDealerMappingSamplePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.processEAdealerMappingData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DealerMappingSample port2 = service1.getDealerMappingSamplePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.processEAdealerMappingData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
