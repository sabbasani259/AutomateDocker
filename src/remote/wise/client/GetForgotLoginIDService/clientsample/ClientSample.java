package remote.wise.client.GetForgotLoginIDService.clientsample;

import remote.wise.client.GetForgotLoginIDService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        GetForgotLoginIDServiceService service1 = new GetForgotLoginIDServiceService();
	        System.out.println("Create Web Service...");
	        GetForgotLoginIDService port1 = service1.getGetForgotLoginIDServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.authenicateLoginIDOrMobileNo(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getForgotLoginIDService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        GetForgotLoginIDService port2 = service1.getGetForgotLoginIDServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.authenicateLoginIDOrMobileNo(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getForgotLoginIDService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
