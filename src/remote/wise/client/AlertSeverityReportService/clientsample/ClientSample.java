package remote.wise.client.AlertSeverityReportService.clientsample;

import remote.wise.client.AlertSeverityReportService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AlertSeverityReportServiceService service1 = new AlertSeverityReportServiceService();
	        System.out.println("Create Web Service...");
	        AlertSeverityReportService port1 = service1.getAlertSeverityReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.alertSeverityReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AlertSeverityReportService port2 = service1.getAlertSeverityReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.alertSeverityReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
