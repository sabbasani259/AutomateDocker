package remote.wise.client.LandmarkActivityReportService.clientsample;

import remote.wise.client.LandmarkActivityReportService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        LandmarkActivityReportServiceService service1 = new LandmarkActivityReportServiceService();
	        System.out.println("Create Web Service...");
	        LandmarkActivityReportService port1 = service1.getLandmarkActivityReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getLandmarkActivityReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        LandmarkActivityReportService port2 = service1.getLandmarkActivityReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getLandmarkActivityReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
