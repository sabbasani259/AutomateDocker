package remote.wise.client.AlertSummaryService.clientsample;

import remote.wise.client.AlertSummaryService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AlertSummaryServiceService service1 = new AlertSummaryServiceService();
	        System.out.println("Create Web Service...");
	        AlertSummaryService port1 = service1.getAlertSummaryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.alertSummaryDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AlertSummaryService port2 = service1.getAlertSummaryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.alertSummaryDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
