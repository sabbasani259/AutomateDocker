package remote.wise.client.RolledOffMachinesService.clientsample;

import remote.wise.client.RolledOffMachinesService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        RolledOffMachinesServiceService service1 = new RolledOffMachinesServiceService();
	        System.out.println("Create Web Service...");
	        RolledOffMachinesService port1 = service1.getRolledOffMachinesServicePort();
	        System.out.println("Call Web Service Operation...");
	       // System.out.println("Server said: " + port1.getRolledOffMachines(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        RolledOffMachinesService port2 = service1.getRolledOffMachinesServicePort();
	        System.out.println("Call Web Service Operation...");
	       // System.out.println("Server said: " + port2.getRolledOffMachines(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
