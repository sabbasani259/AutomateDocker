package remote.wise.client.SMSAlertDetailsService.clientsample;

import remote.wise.client.SMSAlertDetailsService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        SMSAlertsDetailServiceService service1 = new SMSAlertsDetailServiceService();
	        System.out.println("Create Web Service...");
	        SMSAlertsDetailService port1 = service1.getSMSAlertsDetailServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getSMSAlertDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        SMSAlertsDetailService port2 = service1.getSMSAlertsDetailServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getSMSAlertDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
