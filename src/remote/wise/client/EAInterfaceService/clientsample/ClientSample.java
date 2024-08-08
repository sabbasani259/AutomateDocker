package remote.wise.client.EAInterfaceService.clientsample;

import remote.wise.client.EAInterfaceService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        EAInterfaceServiceService service1 = new EAInterfaceServiceService();
	        System.out.println("Create Web Service...");
	        EAInterfaceService port1 = service1.getEAInterfaceServicePort();
	        System.out.println("Call Web Service Operation...");
	      //  System.out.println("Server said: " + port1.getEADetailData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	   //     System.out.println("Server said: " + port1.getEASummaryData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        EAInterfaceService port2 = service1.getEAInterfaceServicePort();
	        System.out.println("Call Web Service Operation...");
	     //   System.out.println("Server said: " + port2.getEADetailData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	      //  System.out.println("Server said: " + port2.getEASummaryData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
