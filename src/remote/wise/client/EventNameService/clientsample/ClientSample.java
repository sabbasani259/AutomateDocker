package remote.wise.client.EventNameService.clientsample;

import remote.wise.client.EventNameService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault{
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        EventNameServiceService service1 = new EventNameServiceService();
	        System.out.println("Create Web Service...");
	        EventNameService port1 = service1.getEventNameServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getReportEvents(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        EventNameService port2 = service1.getEventNameServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getReportEvents(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
