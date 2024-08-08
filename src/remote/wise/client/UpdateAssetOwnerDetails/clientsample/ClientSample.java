package remote.wise.client.UpdateAssetOwnerDetails.clientsample;

import remote.wise.client.UpdateAssetOwnerDetails.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        CurrentAssetOwnerDetailsServiceService service1 = new CurrentAssetOwnerDetailsServiceService();
	        System.out.println("Create Web Service...");
	        CurrentAssetOwnerDetailsService port1 = service1.getCurrentAssetOwnerDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: port1.setCurrentOwnerDetails() is a void method!");
	        System.out.println("Create Web Service...");
	        CurrentAssetOwnerDetailsService port2 = service1.getCurrentAssetOwnerDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: port2.setCurrentOwnerDetails() is a void method!");
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
