package remote.wise.client.MachineRPMBandTrendDataService.clientsample;

import remote.wise.client.MachineRPMBandTrendDataService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineRPMBandTrendDataServiceService service1 = new MachineRPMBandTrendDataServiceService();
	        System.out.println("Create Web Service...");
	        MachineRPMBandTrendDataService port1 = service1.getMachineRPMBandTrendDataServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMachineRPMBandTrendData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getMachineRPMBandTrendDataForAllZones(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineRPMBandTrendDataService port2 = service1.getMachineRPMBandTrendDataServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getMachineRPMBandTrendData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getMachineRPMBandTrendDataForAllZones(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
