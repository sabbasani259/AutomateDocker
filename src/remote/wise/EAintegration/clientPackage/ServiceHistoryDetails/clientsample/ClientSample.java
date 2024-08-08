package remote.wise.EAintegration.clientPackage.ServiceHistoryDetails.clientsample;

import remote.wise.EAintegration.clientPackage.ServiceHistoryDetails.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ServiceHistoryDetailsServiceService service1 = new ServiceHistoryDetailsServiceService();
	        System.out.println("Create Web Service...");
	        ServiceHistoryDetailsService port1 = service1.getServiceHistoryDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	      //DF20190423:IM20018382-Adding additonal field jobCardDetails
	        System.out.println("Server said: " + port1.setServiceHistoryDetails(null,null,null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getServiceHistoryDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ServiceHistoryDetailsService port2 = service1.getServiceHistoryDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	      //DF20190423:IM20018382-Adding additional field jobCardDetails
	        System.out.println("Server said: " + port2.setServiceHistoryDetails(null,null,null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getServiceHistoryDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
