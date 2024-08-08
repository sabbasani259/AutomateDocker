package remote.wise.EAintegration.clientPackage.AssetTypeService.clientsample;

import remote.wise.EAintegration.clientPackage.AssetTypeService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetTypeServiceService service1 = new AssetTypeServiceService();
	        System.out.println("Create Web Service...");
	        AssetTypeService port1 = service1.getAssetTypeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setAssetTypeDetails(null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AssetTypeService port2 = service1.getAssetTypeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setAssetTypeDetails(null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
