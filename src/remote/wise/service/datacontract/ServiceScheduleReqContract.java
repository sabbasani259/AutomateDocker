package remote.wise.service.datacontract;

import remote.wise.businessentity.AssetGroupEntity;

public class ServiceScheduleReqContract {

	private int assetGroupId;
    
      private String SerialNumber;
     

	public String getSerialNumber() {
		return SerialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	
	public int getAssetGroupId() {
		return assetGroupId;
	}

	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}
	
}
