package remote.wise.client.ServiceScheduleService.clientsample;

import remote.wise.client.ServiceScheduleService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        ServiceScheduleServiceService service1 = new ServiceScheduleServiceService();
	        System.out.println("Create Web Service...");
	        ServiceScheduleService port1 = service1.getServiceScheduleServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getServiceScheduleService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        ServiceScheduleService port2 = service1.getServiceScheduleServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getServiceScheduleService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
