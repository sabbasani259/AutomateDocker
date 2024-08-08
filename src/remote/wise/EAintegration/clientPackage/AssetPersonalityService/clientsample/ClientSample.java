package remote.wise.EAintegration.clientPackage.AssetPersonalityService.clientsample;

import remote.wise.EAintegration.clientPackage.AssetPersonalityService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetPersonalityServiceService service1 = new AssetPersonalityServiceService();
	        System.out.println("Create Web Service...");
	        AssetPersonalityService port1 = service1.getAssetPersonalityServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setAssetPersonalityDetails(null,null,null,null,null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AssetPersonalityService port2 = service1.getAssetPersonalityServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setAssetPersonalityDetails(null,null,null,null,null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
