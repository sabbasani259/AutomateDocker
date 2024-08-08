package remote.wise.client.AdminAlertPrefService.clientsample;

import remote.wise.client.AdminAlertPrefService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AdminAlertPrefServiceService service1 = new AdminAlertPrefServiceService();
	        System.out.println("Create Web Service...");
	        AdminAlertPrefService port1 = service1.getAdminAlertPrefServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setAlertMode(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getAlertMode(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AdminAlertPrefService port2 = service1.getAdminAlertPrefServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setAlertMode(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getAlertMode(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
