package remote.wise.client.AssetGroupService.clientsample;

import remote.wise.client.AssetGroupService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetGroupServiceService service1 = new AssetGroupServiceService();
	        System.out.println("Create Web Service...");
	        AssetGroupService port1 = service1.getAssetGroupServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setCustomAssetGroup(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getCustomAssetGroup(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.deleteCustomAssetGroup(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AssetGroupService port2 = service1.getAssetGroupServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setCustomAssetGroup(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getCustomAssetGroup(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.deleteCustomAssetGroup(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
