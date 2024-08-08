package remote.wise.client.AssetDashboardService.clientsample;

import remote.wise.client.AssetDashboardService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetDashboardServiceService service1 = new AssetDashboardServiceService();
	        System.out.println("Create Web Service...");
	        AssetDashboardService port1 = service1.getAssetDashboardServicePort();
	        System.out.println("Call Web Service Operation...");
	     //   System.out.println("Server said: " + port1.getAssetDashboardDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AssetDashboardService port2 = service1.getAssetDashboardServicePort();
	        System.out.println("Call Web Service Operation...");
	      //  System.out.println("Server said: " + port2.getAssetDashboardDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
