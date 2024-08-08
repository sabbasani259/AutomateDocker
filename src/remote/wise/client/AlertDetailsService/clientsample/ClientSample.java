package remote.wise.client.AlertDetailsService.clientsample;

import remote.wise.client.AlertDetailsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AlertDetailsServiceService service1 = new AlertDetailsServiceService();
	        System.out.println("Create Web Service...");
	        AlertDetailsService port1 = service1.getAlertDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setAlertComments(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getAlertDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AlertDetailsService port2 = service1.getAlertDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setAlertComments(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getAlertDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
