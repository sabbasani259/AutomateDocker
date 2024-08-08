package remote.wise.client.MachineHoursReportService.clientsample;

import remote.wise.client.MachineHoursReportService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineHoursReportServiceService service1 = new MachineHoursReportServiceService();
	        System.out.println("Create Web Service...");
	        MachineHoursReportService port1 = service1.getMachineHoursReportServicePort();
	        System.out.println("Call Web Service Operation...");
	       // System.out.println("Server said: " + port1.getMachineHoursReportService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineHoursReportService port2 = service1.getMachineHoursReportServicePort();
	        System.out.println("Call Web Service Operation...");
	       // System.out.println("Server said: " + port2.getMachineHoursReportService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
