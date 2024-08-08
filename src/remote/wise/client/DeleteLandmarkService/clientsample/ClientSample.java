package remote.wise.client.DeleteLandmarkService.clientsample;

import remote.wise.client.DeleteLandmarkService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DeleteLandmarkServiceService service1 = new DeleteLandmarkServiceService();
	        System.out.println("Create Web Service...");
	        DeleteLandmarkService port1 = service1.getDeleteLandmarkServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setDeleteLandmark(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DeleteLandmarkService port2 = service1.getDeleteLandmarkServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setDeleteLandmark(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
