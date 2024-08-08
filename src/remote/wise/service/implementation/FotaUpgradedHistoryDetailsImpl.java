package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import remote.wise.businessobject.FotaUpgradedHistoryBO;
import remote.wise.exception.CustomFault;

import remote.wise.service.datacontract.FotaUpgradedHistoryDetailsReqContract;
import remote.wise.service.datacontract.FotaUpgradedHistoryDetailsRespContract;

/**
 * @author kprabhu5
 *
 */
public class FotaUpgradedHistoryDetailsImpl {
	
	private String serialNumber;
	private String upgradedVersion;
	private String fwVersion;
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getUpgradedVersion() {
		return upgradedVersion;
	}
	public void setUpgradedVersion(String upgradedVersion) {
		this.upgradedVersion = upgradedVersion;
	}
		
	public String getFwVersion() {
		return fwVersion;
	}
	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}
	public List<FotaUpgradedHistoryDetailsRespContract> getFotaUpgradedDetails(FotaUpgradedHistoryDetailsReqContract reqObj) throws CustomFault{
		
		if(reqObj.getSerialNumberList()==null || reqObj.getSerialNumberList().size()<=0){
			throw new CustomFault("Provide Serial number list !!!");
		}
		List<FotaUpgradedHistoryDetailsRespContract> response= new ArrayList<FotaUpgradedHistoryDetailsRespContract>();
		FotaUpgradedHistoryDetailsRespContract respObj= null;
		FotaUpgradedHistoryDetailsImpl implObj = null;
		List<String> serialNumberListFromImpl = new ArrayList<String>();
		List<FotaUpgradedHistoryDetailsImpl> implList = new	FotaUpgradedHistoryBO().getFotaUpgradedHistoryDetails(reqObj.getSerialNumberList());
		if(implList!=null){
			if(implList.size()>0){
				Iterator<FotaUpgradedHistoryDetailsImpl> iterator = implList.iterator();
				while(iterator.hasNext()){
					implObj = iterator.next();
					respObj = new FotaUpgradedHistoryDetailsRespContract();
					respObj.setSerialNumber(implObj.getSerialNumber());
					serialNumberListFromImpl.add(implObj.getSerialNumber());
//					respObj.setUpgradedVersion(implObj.getUpgradedVersion());
					respObj.setFWVersion(implObj.getFwVersion());
					response.add(respObj);				
				}
		   }
		}
		return response;
	}	
}
