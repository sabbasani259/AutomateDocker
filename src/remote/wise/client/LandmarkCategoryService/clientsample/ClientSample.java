package remote.wise.client.LandmarkCategoryService.clientsample;

import remote.wise.client.LandmarkCategoryService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        LandmarkCategoryServiceService service1 = new LandmarkCategoryServiceService();
	        System.out.println("Create Web Service...");
	        LandmarkCategoryService port1 = service1.getLandmarkCategoryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getLandmarkCategory(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.setLandmarkCategory(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        LandmarkCategoryService port2 = service1.getLandmarkCategoryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getLandmarkCategory(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.setLandmarkCategory(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
