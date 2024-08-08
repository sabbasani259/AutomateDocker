package remote.wise.client.TenancyAssetsService.clientsample;

import remote.wise.client.TenancyAssetsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        TenancyAssetsServiceService service1 = new TenancyAssetsServiceService();
	        System.out.println("Create Web Service...");
	        TenancyAssetsService port1 = service1.getTenancyAssetsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getTenancyAssets(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        TenancyAssetsService port2 = service1.getTenancyAssetsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getTenancyAssets(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
