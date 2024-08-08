package remote.EAscheduler.AssetPersonality;

public class ClientSample {

	public static void main(String[] args) {
	        
	        AssetPersonalitySampleService service1 = new AssetPersonalitySampleService();
	        AssetPersonalitySample port1 = service1.getAssetPersonalitySamplePort();
	        port1.processEAassetPersData("AssetPersonality");
	}
}
