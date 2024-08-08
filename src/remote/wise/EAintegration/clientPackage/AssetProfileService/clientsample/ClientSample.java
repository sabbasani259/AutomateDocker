package remote.wise.EAintegration.clientPackage.AssetProfileService.clientsample;

import remote.wise.EAintegration.clientPackage.AssetProfileService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetProfileServiceService service1 = new AssetProfileServiceService();
	        System.out.println("Create Web Service...");
	        AssetProfileService port1 = service1.getAssetProfileServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setAssetProfileDetails(null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AssetProfileService port2 = service1.getAssetProfileServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setAssetProfileDetails(null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
