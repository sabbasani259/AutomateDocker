package remote.wise.client.ActiveMachineListService.clientsample;

import remote.wise.client.ActiveMachineListService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ActiveMachineListServiceService service1 = new ActiveMachineListServiceService();
	        System.out.println("Create Web Service...");
	        ActiveMachineListService port1 = service1.getActiveMachineListServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getActiveMachineList(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ActiveMachineListService port2 = service1.getActiveMachineListServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getActiveMachineList(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
