package remote.wise.EAintegration.clientPackage.EngineTypeService.clientsample;

import remote.wise.EAintegration.clientPackage.EngineTypeService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        EngineTypeServiceService service1 = new EngineTypeServiceService();
	        System.out.println("Create Web Service...");
	        EngineTypeService port1 = service1.getEngineTypeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setEngineTypeDetails(null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        EngineTypeService port2 = service1.getEngineTypeServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setEngineTypeDetails(null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
