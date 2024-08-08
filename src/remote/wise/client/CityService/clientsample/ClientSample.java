package remote.wise.client.CityService.clientsample;

import remote.wise.client.CityService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault{
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        CityServiceService service1 = new CityServiceService();
	        System.out.println("Create Web Service...");
	        CityService port1 = service1.getCityServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getCities());
	        System.out.println("Create Web Service...");
	        CityService port2 = service1.getCityServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getCities());
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
