package remote.wise.client.ForgotPasswordService.clientsample;

import remote.wise.client.ForgotPasswordService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ForgotPasswordServiceService service1 = new ForgotPasswordServiceService();
	        System.out.println("Create Web Service...");
	        ForgotPasswordService port1 = service1.getForgotPasswordServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getForgottenPassword(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ForgotPasswordService port2 = service1.getForgotPasswordServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getForgottenPassword(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
