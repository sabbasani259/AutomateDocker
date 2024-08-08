package remote.wise.client.ReportMasterListService.clientsample;

import remote.wise.client.ReportMasterListService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ReportMasterListServiceService service1 = new ReportMasterListServiceService();
	        System.out.println("Create Web Service...");
	        ReportMasterListService port1 = service1.getReportMasterListServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getReportMasterListService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ReportMasterListService port2 = service1.getReportMasterListServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getReportMasterListService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
