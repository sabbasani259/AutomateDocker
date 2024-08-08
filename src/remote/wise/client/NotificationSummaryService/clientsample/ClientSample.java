package remote.wise.client.NotificationSummaryService.clientsample;

import remote.wise.client.NotificationSummaryService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        NotificationSummaryServiceService service1 = new NotificationSummaryServiceService();
	        System.out.println("Create Web Service...");
	        NotificationSummaryService port1 = service1.getNotificationSummaryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getNotificationSummaryService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        NotificationSummaryService port2 = service1.getNotificationSummaryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getNotificationSummaryService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
