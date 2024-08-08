package remote.wise.client.UtilizationSummaryReportService.clientsample;

import remote.wise.client.UtilizationSummaryReportService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        UtilizationSummaryReportServiceService service1 = new UtilizationSummaryReportServiceService();
	        System.out.println("Create Web Service...");
	        UtilizationSummaryReportService port1 = service1.getUtilizationSummaryReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getUtilizationSummaryReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        UtilizationSummaryReportService port2 = service1.getUtilizationSummaryReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getUtilizationSummaryReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
