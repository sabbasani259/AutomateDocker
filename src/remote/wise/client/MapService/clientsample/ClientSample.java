package remote.wise.client.MapService.clientsample;

import remote.wise.client.MapService.MapService;
import remote.wise.client.MapService.MapServiceService;

public class ClientSample {

	public static void main(String[] args)throws remote.wise.client.MapService.CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MapServiceService service1 = new MapServiceService();
	        System.out.println("Create Web Service...");
	        MapService port1 = service1.getMapServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMap(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MapService port2 = service1.getMapServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMap(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
