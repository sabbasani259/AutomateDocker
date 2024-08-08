package remote.EAscheduler.AssetSaleFromD2C;

public class ClientSample {

	public static void main(String[] args) {
	        AssetSaleFromD2CSampleService service1 = new AssetSaleFromD2CSampleService();
	        AssetSaleFromD2CSample port1 = service1.getAssetSaleFromD2CSamplePort();
	        port1.processEAsaleFromD2C("AssetSaleFromD2C");
	}
}
