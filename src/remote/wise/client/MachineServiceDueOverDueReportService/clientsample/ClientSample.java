package remote.wise.client.MachineServiceDueOverDueReportService.clientsample;

import remote.wise.client.MachineServiceDueOverDueReportService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineServiceDueOverDueReportServiceService service1 = new MachineServiceDueOverDueReportServiceService();
	        System.out.println("Create Web Service...");
	        MachineServiceDueOverDueReportService port1 = service1.getMachineServiceDueOverDueReportServicePort();
	        System.out.println("Call Web Service Operation...");
	       // System.out.println("Server said: " + port1.getMachineServiceDueOverDueCount(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineServiceDueOverDueReportService port2 = service1.getMachineServiceDueOverDueReportServicePort();
	        System.out.println("Call Web Service Operation...");
	       // System.out.println("Server said: " + port2.getMachineServiceDueOverDueCount(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
