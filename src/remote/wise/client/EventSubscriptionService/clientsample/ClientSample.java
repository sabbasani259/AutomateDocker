package remote.wise.client.EventSubscriptionService.clientsample;

import remote.wise.client.EventSubscriptionService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        EventSubscriptionServiceService service1 = new EventSubscriptionServiceService();
	        System.out.println("Create Web Service...");
	        EventSubscriptionService port1 = service1.getEventSubscriptionServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getEventSubscription(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.setEventSubscription(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        EventSubscriptionService port2 = service1.getEventSubscriptionServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getEventSubscription(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.setEventSubscription(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
