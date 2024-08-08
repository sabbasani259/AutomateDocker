package remote.wise.client.UtilizationDetailReportService.clientsample;

import remote.wise.client.UtilizationDetailReportService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        UtilizationDetailReportServiceService service1 = new UtilizationDetailReportServiceService();
	        System.out.println("Create Web Service...");
	        UtilizationDetailReportService port1 = service1.getUtilizationDetailReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getUtilizationDetailReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        UtilizationDetailReportService port2 = service1.getUtilizationDetailReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getUtilizationDetailReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
