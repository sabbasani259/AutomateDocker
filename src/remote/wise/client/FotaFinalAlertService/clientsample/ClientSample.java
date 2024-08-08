package remote.wise.client.FotaFinalAlertService.clientsample;

import remote.wise.client.FotaFinalAlertService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        FotaFinalAlertServiceService service1 = new FotaFinalAlertServiceService();
	        System.out.println("Create Web Service...");
	        FotaFinalAlertService port1 = service1.getFotaFinalAlertServicePort();
	        System.out.println("Call Web Service Operation...");
	       // System.out.println("Server said: " + port1.fotaFinalAlert(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        FotaFinalAlertService port2 = service1.getFotaFinalAlertServicePort();
	        System.out.println("Call Web Service Operation...");
	        //System.out.println("Server said: " + port2.fotaFinalAlert(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
