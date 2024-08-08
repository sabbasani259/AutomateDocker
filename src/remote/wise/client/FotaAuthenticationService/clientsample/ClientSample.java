package remote.wise.client.FotaAuthenticationService.clientsample;

import remote.wise.client.FotaAuthenticationService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        FotaAuthenticationServiceService service1 = new FotaAuthenticationServiceService();
	        System.out.println("Create Web Service...");
	        FotaAuthenticationService port1 = service1.getFotaAuthenticationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.fotaAuthentication(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        FotaAuthenticationService port2 = service1.getFotaAuthenticationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.fotaAuthentication(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
