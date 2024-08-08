package remote.wise.client.MachineSMSReport.clientsample;

import remote.wise.client.MachineSMSReport.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineSMSReportService service1 = new MachineSMSReportService();
	        System.out.println("Create Web Service...");
	        MachineSMSReport port1 = service1.getMachineSMSReportPort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMachineSMSDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineSMSReport port2 = service1.getMachineSMSReportPort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getMachineSMSDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
