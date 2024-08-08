package remote.wise.service.implementation;

import remote.wise.businessobject.AssetDetailsBO;

public class CurrentAssetOwnerDetailsImpl 
{
	public void setOwnerDetails()
	{
		new AssetDetailsBO().populateOwnerDetails(null);
	}
	
	//DF20150311 - Rajani Nagaraju - Updating AssetOwnerSnapshot for a VIN on real time
	public void setVinOwnerDetails(String serialNumber)
	{
		new AssetDetailsBO().populateOwnerDetails(serialNumber);
	}
}
