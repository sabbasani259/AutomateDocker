package remote.wise.client.DetailMachineInfoService.clientsample;

import remote.wise.client.DetailMachineInfoService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DetailMachineInfoServiceService service1 = new DetailMachineInfoServiceService();
	        System.out.println("Create Web Service...");
	        DetailMachineInfoService port1 = service1.getDetailMachineInfoServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getDetailMachineInfo(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DetailMachineInfoService port2 = service1.getDetailMachineInfoServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getDetailMachineInfo(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
