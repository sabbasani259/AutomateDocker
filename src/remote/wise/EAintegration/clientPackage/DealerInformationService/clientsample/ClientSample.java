package remote.wise.EAintegration.clientPackage.DealerInformationService.clientsample;

import remote.wise.EAintegration.clientPackage.DealerInformationService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DealerInformationServiceService service1 = new DealerInformationServiceService();
	        System.out.println("Create Web Service...");
	        DealerInformationService port1 = service1.getDealerInformationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setDealerDetails(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DealerInformationService port2 = service1.getDealerInformationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setDealerDetails(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
