package remote.wise.client.AssetOwnershipManagementService.clientsample;

import remote.wise.client.AssetOwnershipManagementService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetOwnershipManagementServiceService service1 = new AssetOwnershipManagementServiceService();
	        System.out.println("Create Web Service...");
	        AssetOwnershipManagementService port1 = service1.getAssetOwnershipManagementServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setAssetOwnership(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getAssetOwnership(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AssetOwnershipManagementService port2 = service1.getAssetOwnershipManagementServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setAssetOwnership(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getAssetOwnership(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
