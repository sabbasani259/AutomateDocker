package remote.wise.client.DownloadAempService.clientsample;

import remote.wise.client.DownloadAempService.*;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        DownloadAempServiceService service1 = new DownloadAempServiceService();
	        System.out.println("Create Web Service...");
	        DownloadAempService port1 = service1.getDownloadAempServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.getDownloadAemp(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        DownloadAempService port2 = service1.getDownloadAempServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.getDownloadAemp(null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
