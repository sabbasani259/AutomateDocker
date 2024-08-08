package remote.wise.client.UnderUtilizedMachinesService.clientsample;

import remote.wise.client.UnderUtilizedMachinesService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        UnderUtilizedMachinesServiceService service1 = new UnderUtilizedMachinesServiceService();
	        System.out.println("Create Web Service...");
	        UnderUtilizedMachinesService port1 = service1.getUnderUtilizedMachinesServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getUnderUtilizedMachines(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        UnderUtilizedMachinesService port2 = service1.getUnderUtilizedMachinesServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getUnderUtilizedMachines(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
