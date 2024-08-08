package remote.wise.client.UserAssetDetailsService.clientsample;

import remote.wise.client.UserAssetDetailsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        UserAssetDetailsServiceService service1 = new UserAssetDetailsServiceService();
	        System.out.println("Create Web Service...");
	        UserAssetDetailsService port1 = service1.getUserAssetDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getUserAssetDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        UserAssetDetailsService port2 = service1.getUserAssetDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getUserAssetDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
