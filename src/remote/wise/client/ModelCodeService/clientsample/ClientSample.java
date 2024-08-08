package remote.wise.client.ModelCodeService.clientsample;

import remote.wise.client.ModelCodeService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ModelCodeServiceService service1 = new ModelCodeServiceService();
	        System.out.println("Create Web Service...");
	        ModelCodeService port1 = service1.getModelCodeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getModelCodeMap());
	        System.out.println("Create Web Service...");
	        ModelCodeService port2 = service1.getModelCodeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getModelCodeMap());
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
