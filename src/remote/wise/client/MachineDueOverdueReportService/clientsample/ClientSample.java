package remote.wise.client.MachineDueOverdueReportService.clientsample;

import remote.wise.client.MachineDueOverdueReportService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineDueOverdueReportServiceService service1 = new MachineDueOverdueReportServiceService();
	        System.out.println("Create Web Service...");
	        MachineDueOverdueReportService port1 = service1.getMachineDueOverdueReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getDueOverDueMachines(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineDueOverdueReportService port2 = service1.getMachineDueOverdueReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getDueOverDueMachines(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
