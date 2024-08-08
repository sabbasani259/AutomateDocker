package remote.wise.client.AssetGroupUsersService.clientsample;

import remote.wise.client.AssetGroupUsersService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetGroupUsersServiceService service1 = new AssetGroupUsersServiceService();
	        System.out.println("Create Web Service...");
	        AssetGroupUsers port1 = service1.getAssetGroupUsersPort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getAssetGroupUsers(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.setAssetGroupUsers(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AssetGroupUsers port2 = service1.getAssetGroupUsersPort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getAssetGroupUsers(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.setAssetGroupUsers(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
