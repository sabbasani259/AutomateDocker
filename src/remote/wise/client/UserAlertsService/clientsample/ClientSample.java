package remote.wise.client.UserAlertsService.clientsample;

import remote.wise.client.UserAlertsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        UserAlertsServiceService service1 = new UserAlertsServiceService();
	        System.out.println("Create Web Service...");
	        UserAlertsService port1 = service1.getUserAlertsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getUserAlerts(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.setUserAlerts(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        UserAlertsService port2 = service1.getUserAlertsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getUserAlerts(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.setUserAlerts(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
