package remote.wise.client.VinProcessingService.clientsample;

import remote.wise.client.VinProcessingService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        VinProcessingServiceService service1 = new VinProcessingServiceService();
	        System.out.println("Create Web Service...");
	        VinProcessingService port1 = service1.getVinProcessingServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.vinSearch(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        VinProcessingService port2 = service1.getVinProcessingServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.vinSearch(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
