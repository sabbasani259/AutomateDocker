package remote.wise.client.MachineAlertsTrendDataService.clientsample;

import remote.wise.client.MachineAlertsTrendDataService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineAlertsTrendDataServiceService service1 = new MachineAlertsTrendDataServiceService();
	        System.out.println("Create Web Service...");
	        MachineAlertsTrendDataService port1 = service1.getMachineAlertsTrendDataServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMachineAlertsTrendData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getMachineAlertsTrendDataForAllZones(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineAlertsTrendDataService port2 = service1.getMachineAlertsTrendDataServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getMachineAlertsTrendData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getMachineAlertsTrendDataForAllZones(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
