package remote.wise.EAintegration.clientPackage.AssetSaleFromD2C.clientsample;

import remote.wise.EAintegration.clientPackage.AssetSaleFromD2C.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetSaleFromD2CserviceService service1 = new AssetSaleFromD2CserviceService();
	        System.out.println("Create Web Service...");
	        AssetSaleFromD2Cservice port1 = service1.getAssetSaleFromD2CservicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.assetSaleFromDealerToCust(null,null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AssetSaleFromD2Cservice port2 = service1.getAssetSaleFromD2CservicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.assetSaleFromDealerToCust(null,null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
