package remote.wise.client.TenancyDetailsService.clientsample;

import remote.wise.client.TenancyDetailsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault, ParseException_Exception, IOException_Exception {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        TenancyDetailsServiceService service1 = new TenancyDetailsServiceService();
	        System.out.println("Create Web Service...");
	        TenancyDetailsService port1 = service1.getTenancyDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getTenancyDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.setTenancyDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        TenancyDetailsService port2 = service1.getTenancyDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getTenancyDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.setTenancyDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
