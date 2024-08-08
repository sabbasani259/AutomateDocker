package remote.wise.service.implementation;

import remote.wise.businessobject.AssetDetailsBO;

public class AssrtGroupVersionVolumeImpl {
	
	public void setGroupVersionVolume(){
		new AssetDetailsBO().populateGroupVersionVolume();
	}
	public void setGropTypeForDeviceStatusInfo(){
		new AssetDetailsBO().setGropTypeForDeviceStatusInfo();
	}

}
