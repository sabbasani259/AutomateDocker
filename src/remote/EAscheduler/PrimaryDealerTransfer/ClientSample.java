package remote.EAscheduler.PrimaryDealerTransfer;

public class ClientSample {

	public static void main(String[] args) {
	        PrimaryDealerTransferSampleService service1 = new PrimaryDealerTransferSampleService();
	        PrimaryDealerTransferSample port1 = service1.getPrimaryDealerTransferSamplePort();
	        port1.processEADealerTransfer("PrimaryDealerTransfer");
	}
}
