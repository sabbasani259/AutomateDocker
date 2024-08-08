package remote.wise.client.LoginRegistrationService.clientsample;

import remote.wise.client.LoginRegistrationService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        LoginRegistrationServiceService service1 = new LoginRegistrationServiceService();
	        System.out.println("Create Web Service...");
	        LoginRegistrationService port1 = service1.getLoginRegistrationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setSecretQuestionsToUser(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getSecretQuestions());
	        System.out.println("Server said: " + port1.getQuestionsToUser(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        LoginRegistrationService port2 = service1.getLoginRegistrationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setSecretQuestionsToUser(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getSecretQuestions());
	        System.out.println("Server said: " + port2.getQuestionsToUser(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
