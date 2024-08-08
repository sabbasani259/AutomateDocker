package remote.wise.client.NotificationSummaryReportService.clientsample;

import remote.wise.client.AuditLogDetailsService.CustomFault;
import remote.wise.client.NotificationSummaryReportService.*;

public class ClientSample {

	public static void main(String[] args)throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        NotificationSummaryReportServiceService service1 = new NotificationSummaryReportServiceService();
	        System.out.println("Create Web Service...");
	        NotificationSummaryReportService port1 = service1.getNotificationSummaryReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        try {
				System.out.println("Server said: " + port1.notificationSummary(null));
			} catch (remote.wise.client.NotificationSummaryReportService.CustomFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        NotificationSummaryReportService port2 = service1.getNotificationSummaryReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        try {
				System.out.println("Server said: " + port2.notificationSummary(null));
			} catch (remote.wise.client.NotificationSummaryReportService.CustomFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
