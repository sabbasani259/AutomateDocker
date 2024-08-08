package remote.wise.client.RegConfirmationService.clientsample;

import remote.wise.client.RegConfirmationService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        RegConfirmationServiceService service1 = new RegConfirmationServiceService();
	        System.out.println("Create Web Service...");
	        RegConfirmationService port1 = service1.getRegConfirmationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setRegConfirmation(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.deleteRegistrationConfirmation(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getRegistrationConfirmation(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        RegConfirmationService port2 = service1.getRegConfirmationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setRegConfirmation(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.deleteRegistrationConfirmation(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getRegistrationConfirmation(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
