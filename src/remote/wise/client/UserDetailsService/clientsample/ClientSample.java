package remote.wise.client.UserDetailsService.clientsample;

import remote.wise.client.UserDetailsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault, IOException_Exception {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        UserDetailsServiceService service1 = new UserDetailsServiceService();
	        System.out.println("Create Web Service...");
	        UserDetailsService port1 = service1.getUserDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getUserDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.setUserDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        UserDetailsService port2 = service1.getUserDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getUserDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.setUserDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
