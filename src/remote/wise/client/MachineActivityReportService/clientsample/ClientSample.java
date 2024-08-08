package remote.wise.client.MachineActivityReportService.clientsample;

import remote.wise.client.MachineActivityReportService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineActivityReportServiceService service1 = new MachineActivityReportServiceService();
	        System.out.println("Create Web Service...");
	        MachineActivityReportService port1 = service1.getMachineActivityReportServicePort();
	        System.out.println("Call Web Service Operation...");
	      //  System.out.println("Server said: " + port1.getMachineActivityReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineActivityReportService port2 = service1.getMachineActivityReportServicePort();
	        System.out.println("Call Web Service Operation...");
	       // System.out.println("Server said: " + port2.getMachineActivityReport(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
