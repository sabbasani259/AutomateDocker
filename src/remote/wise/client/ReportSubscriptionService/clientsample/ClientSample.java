package remote.wise.client.ReportSubscriptionService.clientsample;

import remote.wise.client.ReportSubscriptionService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ReportSubscriptionServiceService service1 = new ReportSubscriptionServiceService();
	        System.out.println("Create Web Service...");
	        ReportSubscriptionService port1 = service1.getReportSubscriptionServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getReportSubscriptionService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.setReportSubscriptionService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ReportSubscriptionService port2 = service1.getReportSubscriptionServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getReportSubscriptionService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.setReportSubscriptionService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
