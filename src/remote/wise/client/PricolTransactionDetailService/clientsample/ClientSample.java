package remote.wise.client.PricolTransactionDetailService.clientsample;

import remote.wise.client.PricolTransactionDetailService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        PricolTransactionDetailServiceService service1 = new PricolTransactionDetailServiceService();
	        System.out.println("Create Web Service...");
	        PricolTransactionDetailService port1 = service1.getPricolTransactionDetailServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getTransactionDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        PricolTransactionDetailService port2 = service1.getPricolTransactionDetailServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getTransactionDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
