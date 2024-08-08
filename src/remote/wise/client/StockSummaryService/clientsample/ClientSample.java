package remote.wise.client.StockSummaryService.clientsample;

import remote.wise.client.StockSummaryService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        StockSummaryServiceService service1 = new StockSummaryServiceService();
	        System.out.println("Create Web Service...");
	        StockSummaryService port1 = service1.getStockSummaryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getStockSummary(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        StockSummaryService port2 = service1.getStockSummaryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getStockSummary(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
