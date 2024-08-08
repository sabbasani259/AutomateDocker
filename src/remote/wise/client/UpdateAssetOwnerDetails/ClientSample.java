package remote.wise.client.UpdateAssetOwnerDetails;


public class ClientSample {

	public static void main(String[] args) 
	{
	        System.out.println("***********************");
	        System.out.println("Create Web Service Client...");
	        CurrentAssetOwnerDetailsServiceService service1 = new CurrentAssetOwnerDetailsServiceService();
	        System.out.println("Create Web Service...");
	        CurrentAssetOwnerDetailsService port1 = service1.getCurrentAssetOwnerDetailsServicePort();
	        port1.setCurrentOwnerDetails();
	}
}
