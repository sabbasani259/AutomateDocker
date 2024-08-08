package remote.wise.client.GetDealersForZoneService.clientsample;

import remote.wise.client.GetDealersForZoneService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        GetDealersForZoneServiceService service1 = new GetDealersForZoneServiceService();
	        System.out.println("Create Web Service...");
	        GetDealersForZoneService port1 = service1.getGetDealersForZoneServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getDealersForZone(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        GetDealersForZoneService port2 = service1.getGetDealersForZoneServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getDealersForZone(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
