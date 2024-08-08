package remote.wise.client.FotaUpdateService.clientsample;

import remote.wise.client.FotaUpdateService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        FotaUpdateServiceService service1 = new FotaUpdateServiceService();
	        System.out.println("Create Web Service...");
	        FotaUpdateService port1 = service1.getFotaUpdateServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.fotaUpdate(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        FotaUpdateService port2 = service1.getFotaUpdateServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.fotaUpdate(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
