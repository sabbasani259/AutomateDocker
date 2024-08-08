package remote.wise.client.SearchService.clientsample;

import remote.wise.client.SearchService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        SearchServiceService service1 = new SearchServiceService();
	        System.out.println("Create Web Service...");
	        SearchService port1 = service1.getSearchServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMGLandMarkDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        SearchService port2 = service1.getSearchServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getMGLandMarkDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
