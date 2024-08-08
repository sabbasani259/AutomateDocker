package remote.wise.client.PricolRollOffService.clientsample;

import remote.wise.client.PricolRollOffService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        PricolRollOffServiceService service1 = new PricolRollOffServiceService();
	        System.out.println("Create Web Service...");
	        PricolRollOffService port1 = service1.getPricolRollOffServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.rollOffPricolDevice(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        PricolRollOffService port2 = service1.getPricolRollOffServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.rollOffPricolDevice(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
