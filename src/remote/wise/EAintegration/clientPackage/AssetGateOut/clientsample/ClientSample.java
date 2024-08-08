package remote.wise.EAintegration.clientPackage.AssetGateOut.clientsample;

import remote.wise.EAintegration.clientPackage.AssetGateOut.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetGateoutServiceService service1 = new AssetGateoutServiceService();
	        System.out.println("Create Web Service...");
	        AssetGateoutService port1 = service1.getAssetGateoutServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setAssetGateoutService(null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AssetGateoutService port2 = service1.getAssetGateoutServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setAssetGateoutService(null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
