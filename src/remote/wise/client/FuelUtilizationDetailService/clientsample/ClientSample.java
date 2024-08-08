package remote.wise.client.FuelUtilizationDetailService.clientsample;

import remote.wise.client.FuelUtilizationDetailService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        FuelUtilizationDetailServiceService service1 = new FuelUtilizationDetailServiceService();
	        System.out.println("Create Web Service...");
	        FuelUtilizationDetailService port1 = service1.getFuelUtilizationDetailServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getFuelUtilizationDetail(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        FuelUtilizationDetailService port2 = service1.getFuelUtilizationDetailServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getFuelUtilizationDetail(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
