package remote.wise.client.FilterService.clientsample;

import remote.wise.client.FilterService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        FilterServiceService service1 = new FilterServiceService();
	        System.out.println("Create Web Service...");
	        FilterService port1 = service1.getFilterServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getDealers(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        FilterService port2 = service1.getFilterServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getDealers(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
