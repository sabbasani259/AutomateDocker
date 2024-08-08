package remote.wise.client.AssetGroupVersionVolumeSummaryService;

import remote.wise.client.AssetGroupVersionVolumeSummaryService.*;

public class ClientSample {

	public static void main(String[] args) {
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        AssetGroupVersionVolumeSummaryServiceService service1 = new AssetGroupVersionVolumeSummaryServiceService();
	        System.out.println("Create Web Service...");
	        AssetGroupVersionVolumeSummaryService port1 = service1.getAssetGroupVersionVolumeSummaryServicePort();
	        port1.setGropTypeForDeviceStatusInfo();
	       
	}
}
