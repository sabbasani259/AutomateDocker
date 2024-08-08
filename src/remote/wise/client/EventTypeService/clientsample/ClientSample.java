package remote.wise.client.EventTypeService.clientsample;

import remote.wise.client.EventTypeService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault{
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        EventTypeServiceService service1 = new EventTypeServiceService();
	        System.out.println("Create Web Service...");
	        EventTypeService port1 = service1.getEventTypeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getEventTypes());
	        System.out.println("Create Web Service...");
	        EventTypeService port2 = service1.getEventTypeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getEventTypes());
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
