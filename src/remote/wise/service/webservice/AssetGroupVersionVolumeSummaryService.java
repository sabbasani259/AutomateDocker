package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

import remote.wise.service.implementation.AssrtGroupVersionVolumeImpl;

@WebService(name = "AssetGroupVersionVolumeSummaryService")
public class AssetGroupVersionVolumeSummaryService {
	@WebMethod(operationName = "setGroupVersionVolume", action = "setGroupVersionVolume")
	public void setGroupVersionVolume(){
		new AssrtGroupVersionVolumeImpl().setGroupVersionVolume();
	}
	public void setGropTypeForDeviceStatusInfo(){
		new AssrtGroupVersionVolumeImpl().setGropTypeForDeviceStatusInfo();
	}

}
