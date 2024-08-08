package remote.wise.client.AlertThresholdSettingsService.clientsample;

import remote.wise.client.AlertThresholdSettingsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AlertThresholdSettingsServiceService service1 = new AlertThresholdSettingsServiceService();
	        System.out.println("Create Web Service...");
	        AlertThresholdService port1 = service1.getAlertThresholdServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setAlertThreshold(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getAlertThreshold(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AlertThresholdService port2 = service1.getAlertThresholdServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setAlertThreshold(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getAlertThreshold(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
