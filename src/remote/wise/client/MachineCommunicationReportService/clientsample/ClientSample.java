package remote.wise.client.MachineCommunicationReportService.clientsample;

import remote.wise.client.MachineCommunicationReportService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineCommunicationReportServiceService service1 = new MachineCommunicationReportServiceService();
	        System.out.println("Create Web Service...");
	        MachineCommunicationReportService port1 = service1.getMachineCommunicationReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMachineCommunicationReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineCommunicationReportService port2 = service1.getMachineCommunicationReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getMachineCommunicationReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
