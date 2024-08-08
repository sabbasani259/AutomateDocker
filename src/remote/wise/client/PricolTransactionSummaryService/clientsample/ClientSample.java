package remote.wise.client.PricolTransactionSummaryService.clientsample;

import remote.wise.client.PricolTransactionSummaryService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        PricolTransactionSummaryServiceService service1 = new PricolTransactionSummaryServiceService();
	        System.out.println("Create Web Service...");
	        PricolTransactionSummaryService port1 = service1.getPricolTransactionSummaryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getVinList(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        PricolTransactionSummaryService port2 = service1.getPricolTransactionSummaryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getVinList(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
