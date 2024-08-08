package remote.wise.client.UserAuthenticationService.clientsample;

import remote.wise.client.UserAuthenticationService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault, IOException_Exception {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        UserAuthenticationServiceService service1 = new UserAuthenticationServiceService();
	        System.out.println("Create Web Service...");
	        UserAuthenticationService port1 = service1.getUserAuthenticationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.userAuthentication(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        UserAuthenticationService port2 = service1.getUserAuthenticationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.userAuthentication(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
