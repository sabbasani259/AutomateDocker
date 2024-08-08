package remote.wise.client.DeleteLandmarkCategoryService.clientsample;

import remote.wise.client.DeleteLandmarkCategoryService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DeleteLandmarkCategoryServiceService service1 = new DeleteLandmarkCategoryServiceService();
	        System.out.println("Create Web Service...");
	        DeleteLandmarkCategoryService port1 = service1.getDeleteLandmarkCategoryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setDeleteLandmarkCategory(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DeleteLandmarkCategoryService port2 = service1.getDeleteLandmarkCategoryServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setDeleteLandmarkCategory(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
