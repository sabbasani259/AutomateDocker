package remote.wise.client.UtilizationDetailService.clientsample;

import remote.wise.client.UtilizationDetailService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        UtilizationDetailServiceService service1 = new UtilizationDetailServiceService();
	        System.out.println("Create Web Service...");
	        UtilizationDetailService port1 = service1.getUtilizationDetailServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getUtilizationDetailService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        UtilizationDetailService port2 = service1.getUtilizationDetailServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getUtilizationDetailService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
