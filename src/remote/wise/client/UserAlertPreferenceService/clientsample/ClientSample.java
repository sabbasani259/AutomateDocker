package remote.wise.client.UserAlertPreferenceService.clientsample;

import remote.wise.client.UserAlertPreferenceService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        UserAlertPreferenceServiceService service1 = new UserAlertPreferenceServiceService();
	        System.out.println("Create Web Service...");
	        UserAlertPreferenceService port1 = service1.getUserAlertPreferenceServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setAdminPreference(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.setUserAlertPreference(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getUserAlertPreference(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        UserAlertPreferenceService port2 = service1.getUserAlertPreferenceServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setAdminPreference(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.setUserAlertPreference(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getUserAlertPreference(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
