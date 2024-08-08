package remote.wise.client.ResetPasswordService.clientsample;

import remote.wise.client.ResetPasswordService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ResetPasswordServiceService service1 = new ResetPasswordServiceService();
	        System.out.println("Create Web Service...");
	        ResetPasswordService port1 = service1.getResetPasswordServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.resetPassword(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ResetPasswordService port2 = service1.getResetPasswordServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.resetPassword(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
