package remote.wise.EAintegration.clientPackage.ZonalInformationService.clientsample;

import remote.wise.EAintegration.clientPackage.ZonalInformationService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault, ParseException_Exception, IOException_Exception {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ZonalInformationServiceService service1 = new ZonalInformationServiceService();
	        System.out.println("Create Web Service...");
	        ZonalInformationService port1 = service1.getZonalInformationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setZonalDetails(null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ZonalInformationService port2 = service1.getZonalInformationServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setZonalDetails(null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
