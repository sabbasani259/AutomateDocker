package remote.wise.client.SMSAlertsService.clientsample;

import remote.wise.client.SMSAlertsService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        SMSAlertsServiceService service1 = new SMSAlertsServiceService();
	        System.out.println("Create Web Service...");
	        SMSAlertsService port1 = service1.getSMSAlertsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getSMSAlerts(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        SMSAlertsService port2 = service1.getSMSAlertsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getSMSAlerts(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
