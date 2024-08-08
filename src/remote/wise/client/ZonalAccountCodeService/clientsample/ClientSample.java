package remote.wise.client.ZonalAccountCodeService.clientsample;

import remote.wise.client.ZonalAccountCodeService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault{
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ZonalAccountCodeServiceService service1 = new ZonalAccountCodeServiceService();
	        System.out.println("Create Web Service...");
	        ZonalAccountCodeService port1 = service1.getZonalAccountCodeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getZoneDealerAccounts(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ZonalAccountCodeService port2 = service1.getZonalAccountCodeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getZoneDealerAccounts(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
