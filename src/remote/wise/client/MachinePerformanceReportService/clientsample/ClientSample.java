package remote.wise.client.MachinePerformanceReportService.clientsample;

import remote.wise.client.MachinePerformanceReportService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachinePerformanceReportServiceService service1 = new MachinePerformanceReportServiceService();
	        System.out.println("Create Web Service...");
	        MachinePerformanceReportService port1 = service1.getMachinePerformanceReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.machinePerformanceReportService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachinePerformanceReportService port2 = service1.getMachinePerformanceReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.machinePerformanceReportService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
