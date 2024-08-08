package remote.wise.EAintegration.clientPackage.PrimaryDealerTransfer.clientsample;

import remote.wise.EAintegration.clientPackage.PrimaryDealerTransfer.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        PrimaryDealerTransferServiceService service1 = new PrimaryDealerTransferServiceService();
	        System.out.println("Create Web Service...");
	        PrimaryDealerTransferService port1 = service1.getPrimaryDealerTransferServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.primaryDealerTransfer(null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        PrimaryDealerTransferService port2 = service1.getPrimaryDealerTransferServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.primaryDealerTransfer(null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
