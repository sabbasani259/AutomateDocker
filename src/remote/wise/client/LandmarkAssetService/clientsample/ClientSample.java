package remote.wise.client.LandmarkAssetService.clientsample;

import remote.wise.client.LandmarkAssetService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        LandmarkAssetServiceService service1 = new LandmarkAssetServiceService();
	        System.out.println("Create Web Service...");
	        LandmarkAssetService port1 = service1.getLandmarkAssetServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setLandmarkAsset(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getLandmarkAsset(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        LandmarkAssetService port2 = service1.getLandmarkAssetServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setLandmarkAsset(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getLandmarkAsset(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
