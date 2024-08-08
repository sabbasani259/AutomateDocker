package remote.wise.client.FleetSummaryService.clientsample;

import remote.wise.client.FleetSummaryService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        FleetSummaryServiceService service1 = new FleetSummaryServiceService();
	        System.out.println("Create Web Service...");
	        FleetSummaryService port1 = service1.getFleetSummaryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getFleetSummaryService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        FleetSummaryService port2 = service1.getFleetSummaryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getFleetSummaryService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
