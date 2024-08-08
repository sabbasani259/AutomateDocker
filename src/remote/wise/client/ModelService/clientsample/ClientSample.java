package remote.wise.client.ModelService.clientsample;

import remote.wise.client.ModelService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ModelServiceService service1 = new ModelServiceService();
	        System.out.println("Create Web Service...");
	        ModelService port1 = service1.getModelServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getModelMap());
	        System.out.println("Create Web Service...");
	        ModelService port2 = service1.getModelServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getModelMap());
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
