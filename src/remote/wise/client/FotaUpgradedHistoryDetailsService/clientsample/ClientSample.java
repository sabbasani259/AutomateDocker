package remote.wise.client.FotaUpgradedHistoryDetailsService.clientsample;

import remote.wise.client.FotaUpgradedHistoryDetailsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        FotaUpgradedHistoryDetailsServiceService service1 = new FotaUpgradedHistoryDetailsServiceService();
	        System.out.println("Create Web Service...");
	        FotaUpgradedHistoryDetailsService port1 = service1.getFotaUpgradedHistoryDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getFotaUpgradedDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        FotaUpgradedHistoryDetailsService port2 = service1.getFotaUpgradedHistoryDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getFotaUpgradedDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
