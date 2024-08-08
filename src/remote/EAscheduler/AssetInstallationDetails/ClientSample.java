package remote.EAscheduler.AssetInstallationDetails;

public class ClientSample {

	public static void main(String[] args) {
	        AssetInstallationDetailsSampleService service1 = new AssetInstallationDetailsSampleService();
	        AssetInstallationDetailsSample port1 = service1.getAssetInstallationDetailsSamplePort();
	        port1.processEAassetInstData("AssetInstallationDetails");
	}
}
