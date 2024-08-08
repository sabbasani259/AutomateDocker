package remote.wise.client.AuditLogDetailsService.clientsample;

import remote.wise.client.AuditLogDetailsService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AuditLogDetailsServiceService service1 = new AuditLogDetailsServiceService();
	        System.out.println("Create Web Service...");
	        AuditLogDetailsService port1 = service1.getAuditLogDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getAuditLogDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        AuditLogDetailsService port2 = service1.getAuditLogDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getAuditLogDetails(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
