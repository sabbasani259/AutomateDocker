package remote.wise.EAintegration.clientPackage.AssetInstallationDetails.clientsample;

import remote.wise.EAintegration.clientPackage.AssetInstallationDetails.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        InstallationDateDetailsServiceService service1 = new InstallationDateDetailsServiceService();
	        System.out.println("Create Web Service...");
	        InstallationDateDetailsService port1 = service1.getInstallationDateDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port1.setAssetServiceSchedule(null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("Create Web Service...");
	        InstallationDateDetailsService port2 = service1.getInstallationDateDetailsServicePort();
	        System.out.println("Call Web Service Operation...");
	        System.out.println("Server said: " + port2.setAssetServiceSchedule(null,null,null,null,null,null,null,null));
	        //Please input the parameters instead of 'null' for the upper method!
	
	        System.out.println("***********************");
	        System.out.println("Call Over!");
	}
}
