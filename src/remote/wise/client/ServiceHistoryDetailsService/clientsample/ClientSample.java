package remote.wise.client.ServiceHistoryDetailsService.clientsample;

import remote.wise.client.ServiceHistoryDetailsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ServiceHistoryDetailsServiceService service1 = new ServiceHistoryDetailsServiceService();
	        System.out.println("Create Web Service...");
	        ServiceHistoryDetailsService port1 = service1.getServiceHistoryDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setServiceHistoryDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getServiceHistoryDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ServiceHistoryDetailsService port2 = service1.getServiceHistoryDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setServiceHistoryDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getServiceHistoryDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
