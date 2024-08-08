package remote.wise.client.AssetExtendedService.clientsample;

import remote.wise.client.AssetExtendedService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetExtendedServiceService service1 = new AssetExtendedServiceService();
	        System.out.println("Create Web Service...");
	        AssetExtendedService port1 = service1.getAssetExtendedServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.updateCHMRDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.setAssetExtendedDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getAssetExtendedDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AssetExtendedService port2 = service1.getAssetExtendedServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.updateCHMRDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.setAssetExtendedDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getAssetExtendedDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
