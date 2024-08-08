package remote.wise.client.MachineBillingReportService.clientsample;

import remote.wise.client.MachineBillingReportService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineBillingReportServiceService service1 = new MachineBillingReportServiceService();
	        System.out.println("Create Web Service...");
	        MachineBillingReportService port1 = service1.getMachineBillingReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMachineBillingReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineBillingReportService port2 = service1.getMachineBillingReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getMachineBillingReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
