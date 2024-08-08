package remote.wise.client.StateService.clientsample;

import remote.wise.client.StateService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault{
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        StateServiceService service1 = new StateServiceService();
	        System.out.println("Create Web Service...");
	        StateService port1 = service1.getStateServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getStates());
	        System.out.println("Create Web Service...");
	        StateService port2 = service1.getStateServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getStates());
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
