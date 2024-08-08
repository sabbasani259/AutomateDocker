package remote.wise.client.DealersUnderZoneService.clientsample;

import remote.wise.client.DealersUnderZoneService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DealersUnderZoneServiceService service1 = new DealersUnderZoneServiceService();
	        System.out.println("Create Web Service...");
	        DealersUnderZoneService port1 = service1.getDealersUnderZoneServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getDealers(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DealersUnderZoneService port2 = service1.getDealersUnderZoneServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getDealers(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
