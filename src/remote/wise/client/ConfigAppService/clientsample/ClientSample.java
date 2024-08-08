package remote.wise.client.ConfigAppService.clientsample;

import remote.wise.client.ConfigAppService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ConfigAppServiceService service1 = new ConfigAppServiceService();
	        System.out.println("Create Web Service...");
	        ConfigAppService port1 = service1.getConfigAppServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setConfigAppService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getConfigAppService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ConfigAppService port2 = service1.getConfigAppServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setConfigAppService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getConfigAppService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
