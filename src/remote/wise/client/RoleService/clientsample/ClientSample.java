package remote.wise.client.RoleService.clientsample;

import remote.wise.client.RoleService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        RoleServiceService service1 = new RoleServiceService();
	        System.out.println("Create Web Service...");
	        RoleService port1 = service1.getRoleServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getRoles());
	        System.out.println("Create Web Service...");
	        RoleService port2 = service1.getRoleServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getRoles());
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
