package remote.wise.client.MachineHealthDetailsService.clientsample;

import remote.wise.client.MachineHealthDetailsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MachineHealthDetailsServiceService service1 = new MachineHealthDetailsServiceService();
	        System.out.println("Create Web Service...");
	        MachineHealthDetailsService port1 = service1.getMachineHealthDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getMachineHealthDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MachineHealthDetailsService port2 = service1.getMachineHealthDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getMachineHealthDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
