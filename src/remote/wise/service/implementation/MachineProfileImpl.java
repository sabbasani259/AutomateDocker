package remote.wise.service.implementation;

import java.sql.Timestamp;

import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.MachineProfileRespContract;
import remote.wise.service.datacontract.MachineProfileReqContract;
/**
 * 
 * @author tejgm
 * This implementation gets the details on the individual machines including the duration taken by the asset
 */
public class MachineProfileImpl {
	private int assetGroupId;
	private String profileName;
	private String assetGroupCode;
	private String operatingStartTime;
	private String  operatingEndTime;
	
	public int getAssetGroupId() {
		return assetGroupId;
	}
	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getAssetGroupCode() {
		return assetGroupCode;
	}
	public void setAssetGroupCode(String assetGroupCode) {
		this.assetGroupCode = assetGroupCode;
	}


	public String getOperatingStartTime() {
		return operatingStartTime;
	}
	public void setOperatingStartTime(String operatingStartTime) {
		this.operatingStartTime = operatingStartTime;
	}
	public String getOperatingEndTime() {
		return operatingEndTime;
	}
	public void setOperatingEndTime(String operatingEndTime) {
		this.operatingEndTime = operatingEndTime;
	}
	/**
	 * 
	 * @param reqObj is provided to get profile of the machines that belongs to the group of asset's
	 * @return listofResponse provides the details on the individual machines including the duration taken by the asset
	 * @throws CustomFault
	 */
	public List<MachineProfileRespContract> getMachineProfile(MachineProfileReqContract reqObj)throws CustomFault

	{
		List<MachineProfileRespContract> listofResponse=new LinkedList<MachineProfileRespContract>();
		AssetDetailsBO assetDetailsBO=new AssetDetailsBO();
		List<MachineProfileImpl> responseOfBo=assetDetailsBO.getMachineProfile(reqObj.getLoginId());
		for(int i=0;i<responseOfBo.size();i++)
		{
			MachineProfileRespContract response=new MachineProfileRespContract();
			response.setAssetGroupId(responseOfBo.get(i).getAssetGroupId());
			response.setProfileName(responseOfBo.get(i).getProfileName());
			response.setAssetGroupCode(responseOfBo.get(i).getAssetGroupCode());
			response.setAsseetOperatingStartTime(responseOfBo.get(i).getOperatingStartTime().toString());
			response.setAsseetOperatingEndTime(responseOfBo.get(i).getOperatingEndTime().toString());
			
			listofResponse.add(response);
		}
		return listofResponse;
	}
	
	/**
	 * 
	 * @param respObj provided as input to set a profile of the asset
	 * @return machineProfileResponse sets the machines profile based on the respObj that was passed
	 * @throws CustomFault
	 */
	
	public String setMachineProfileService(MachineProfileRespContract respObj)throws CustomFault
	{
		AssetDetailsBO assetDetailsBO=new AssetDetailsBO();
		String response=assetDetailsBO.setMachineProfileService(respObj.getAssetGroupId(),respObj.getProfileName(),respObj.getAssetGroupCode(),respObj.getAsseetOperatingStartTime(),respObj.getAsseetOperatingEndTime());
		return response;
	}
	
}
