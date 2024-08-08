package remote.EAscheduler.AssetGateOut;

public class ClientSample {

	public static void main(String[] args) {
	       
	        AssetGateOutSampleService service1 = new AssetGateOutSampleService();
	        AssetGateOutSample port1 = service1.getAssetGateOutSamplePort();
	        port1.processEAassetGateOut("AssetGateOut");
	}
}
