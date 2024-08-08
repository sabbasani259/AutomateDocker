package remote.wise.client.MachineHoursServiceReportService.clientsample;

import remote.wise.client.MachineHoursServiceReportService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault{
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineHoursServiceReportServiceService service1 = new MachineHoursServiceReportServiceService();
	        System.out.println("Create Web Service...");
	        MachineHoursServiceReportService port1 = service1.getMachineHoursServiceReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMachineHoursServiceReportService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineHoursServiceReportService port2 = service1.getMachineHoursServiceReportServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getMachineHoursServiceReportService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
