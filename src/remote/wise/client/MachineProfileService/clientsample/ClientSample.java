package remote.wise.client.MachineProfileService.clientsample;

import remote.wise.client.MachineProfileService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineProfileServiceService service1 = new MachineProfileServiceService();
	        System.out.println("Create Web Service...");
	        MachineProfileService port1 = service1.getMachineProfileServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setMachineProfileService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getMachineProfileService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineProfileService port2 = service1.getMachineProfileServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setMachineProfileService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getMachineProfileService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
