package remote.wise.client.FleetSummaryReportService.clientsample;

import remote.wise.client.FleetSummaryReportService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        FleetSummaryReportServiceService service1 = new FleetSummaryReportServiceService();
	        System.out.println("Create Web Service...");
	        FleetSummaryReportService port1 = service1.getFleetSummaryReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getFleetSummaryReportService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        FleetSummaryReportService port2 = service1.getFleetSummaryReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getFleetSummaryReportService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
