package remote.wise.EAintegration.clientPackage.CustomerInformationService.clientsample;

import remote.wise.EAintegration.clientPackage.CustomerInformationService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        CustomerInformationServiceService service1 = new CustomerInformationServiceService();
	        System.out.println("Create Web Service...");
	        CustomerInformationService port1 = service1.getCustomerInformationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setCustomerDetails(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        CustomerInformationService port2 = service1.getCustomerInformationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setCustomerDetails(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
