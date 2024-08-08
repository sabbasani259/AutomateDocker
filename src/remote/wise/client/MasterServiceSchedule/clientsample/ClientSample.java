package remote.wise.client.MasterServiceSchedule.clientsample;

import remote.wise.client.MasterServiceSchedule.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        MasterServiceScheduleService service1 = new MasterServiceScheduleService();
	        System.out.println("Create Web Service...");
	        MasterServiceSchedule port1 = service1.getMasterServiceSchedulePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setMasterServiceScheduleService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port1.getMasterServiceSchedule(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        MasterServiceSchedule port2 = service1.getMasterServiceSchedulePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setMasterServiceScheduleService(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Server said: " + port2.getMasterServiceSchedule(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
