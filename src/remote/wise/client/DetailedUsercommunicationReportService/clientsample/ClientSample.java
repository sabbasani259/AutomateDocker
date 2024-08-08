package remote.wise.client.DetailedUsercommunicationReportService.clientsample;

import remote.wise.client.DetailedUsercommunicationReportService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DetailedUsercommunicationReportServiceService service1 = new DetailedUsercommunicationReportServiceService();
	        System.out.println("Create Web Service...");
	        DetailedUsercommunicationReportService port1 = service1.getDetailedUsercommunicationReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getDetailedUsercommunicationReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DetailedUsercommunicationReportService port2 = service1.getDetailedUsercommunicationReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getDetailedUsercommunicationReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
