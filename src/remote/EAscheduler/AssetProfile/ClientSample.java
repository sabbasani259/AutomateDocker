package remote.EAscheduler.AssetProfile;

public class ClientSample {

	public static void main(String[] args) {
	        AssetProfileSampleService service1 = new AssetProfileSampleService();
	        AssetProfileSample port1 = service1.getAssetProfileSamplePort();
	        port1.processEAassetProfileData("AssetProfile");
	}
}
