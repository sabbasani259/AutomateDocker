package remote.wise.client.DeleteUserService.clientsample;

import remote.wise.client.DeleteUserService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DeleteUserServiceService service1 = new DeleteUserServiceService();
	        System.out.println("Create Web Service...");
	        DeleteUserService port1 = service1.getDeleteUserServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.deleteUser(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DeleteUserService port2 = service1.getDeleteUserServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.deleteUser(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
