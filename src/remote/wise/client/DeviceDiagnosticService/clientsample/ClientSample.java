package remote.wise.client.DeviceDiagnosticService.clientsample;

import remote.wise.client.DeviceDiagnosticService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DeviceDiagnosticServiceService service1 = new DeviceDiagnosticServiceService();
	        System.out.println("Create Web Service...");
	        DeviceDiagnosticService port1 = service1.getDeviceDiagnosticServicePort();
	        System.out.println("Call Web Service Operation...");
	        //System.out.println("Server said: " + port1.setEventData(null,Integer.parseInt(args[0])));
	        //Please input the parameters instead of 'null' for the upper method!
	
	       // System.out.println("Server said: " + port1.setDeviceData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DeviceDiagnosticService port2 = service1.getDeviceDiagnosticServicePort();
	        System.out.println("Call Web Service Operation...");
	       // System.out.println("Server said: " + port2.setEventData(null,Integer.parseInt(args[1])));
	        //Please input the parameters instead of 'null' for the upper method!
	
	       // System.out.println("Server said: " + port2.setDeviceData(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
