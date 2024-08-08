/**
 * CR428 : 20230830 : Dhiraj Kumar : Sea Ports (Landmark) Configurations
 */
package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.LandmarkEntity;
import remote.wise.businessobject.LandmarkCategoryBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.LandmarkAssetReqContract;
import remote.wise.service.datacontract.LandmarkAssetRespContract;
import remote.wise.service.datacontract.LandmarkCategoryReqContract;
import remote.wise.service.datacontract.LandmarkCategoryRespContract;
import remote.wise.service.datacontract.SetSeaportLandmarkAssetReqContract;
//import remote.wise.util.WiseLogger;
/**
 * Implementation class will allow to get and set LandmarkAsset
  *  @author jgupta41
 */
public class LandmarkAssetImpl 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//public static WiseLogger businessError = WiseLogger.getLogger("LandmarkAssetImpl:","businessError");
	private int Landmark_id;
	private List<String>  Serial_number;
	//private String  Serial_number;
	//private List<String> assetGroupName;
	String assetGroupName;
	

	public List<String> getSerial_number() {
		return Serial_number;
	}


	public void setSerial_number(List<String> serial_number) {
		Serial_number = serial_number;
	}


	public int getLandmark_id() {
		return Landmark_id;
	}


	/*public List<String> getAssetGroupName() {
		return assetGroupName;
	}


	public void setAssetGroupName(List<String> assetGroupName) {
		this.assetGroupName = assetGroupName;
	}*/
	public String getAssetGroupName() {
		return assetGroupName;
	}


	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}

	public void setLandmark_id(int landmark_id) {
		Landmark_id = landmark_id;
	}

    
	


//********************************************** get Asset for given Landmark *****************************************
/**
 * This method will allow to get Asset for a given Landmark
 * @param landmarkAssetReq  :Asset for a specific Landmark can be retrieved by specifying the required Landmark through this reqObj
 * @return respList: Assets  for given Landmark are returned
 * @throws CustomFault :custom exception is thrown when the login_id is not specified or invalid,Landmark_id is invalid when specified
 */

	public List<LandmarkAssetRespContract> getLandmarkAsset(LandmarkAssetReqContract landmarkAssetReq ,int pagenumber , int pagesize)throws CustomFault
	{
		List<LandmarkAssetRespContract> respList = new LinkedList<LandmarkAssetRespContract>();
		LandmarkCategoryBO landmarkBO= new LandmarkCategoryBO();
		List<LandmarkAssetImpl> landmarkAssetImpl = new LinkedList<LandmarkAssetImpl>();
		
		//landmarkAssetImpl = landmarkBO.getLandmarkAssetObj(landmarkAssetReq.getLogin_id(),landmarkAssetReq.getLandmark_id() ,landmarkAssetReq.getPageNumber() , landmarkAssetReq.getPageSize());
		landmarkAssetImpl = landmarkBO.getLandmarkAssetObj(landmarkAssetReq.getLogin_id(),landmarkAssetReq.getLandmark_id() , pagenumber , pagesize);
		
		
		for(int i=0;i<landmarkAssetImpl.size();i++)
		{
			LandmarkAssetRespContract  respContractObj=new LandmarkAssetRespContract();
		respContractObj.setLandmark_id(landmarkAssetImpl.get(i).getLandmark_id());
		respContractObj.setSerial_number(landmarkAssetImpl.get(i).getSerial_number());
	
		respList.add(respContractObj);
		}
		return respList;
	}
	


	//*************************************************END of  get Landmark for a given Landmark************************************
	//********************************************** set Asset for given Landmark *****************************************
	/**
	 * This method will allow to set Asset for a given Landmark
	 * @param landmarkAssetResp :Landmark for which asset to be set.
	 * @return flag :Returns the status String as either SUCCESS/FAILURE 
	 * @throws CustomFault :custom exception is thrown when the Landmark_id is not specified or invalid,Landmark_id is invalid when specified
	 */
	
	public String setLandmarkAsset(	LandmarkAssetRespContract landmarkAssetResp)throws CustomFault
	{
		LandmarkCategoryBO landmarkCategoryBO=new 	LandmarkCategoryBO();
		String flag=landmarkCategoryBO.setLandmarkAsset(landmarkAssetResp.getLogin_Id(),landmarkAssetResp.getSerial_number(),landmarkAssetResp.getLandmark_id(),landmarkAssetResp.getAsset_group_id());
		return flag;
	}
	//*************************************************END of  set Landmark for a given Landmark************************************
	
	//CR428.sn
	public String setSeaportLandmarkAsset(SetSeaportLandmarkAssetReqContract seaportLandmarkAsset) throws CustomFault
	{
		LandmarkCategoryBO landmarkCategoryBO = new LandmarkCategoryBO();
		String status = landmarkCategoryBO.setSeaportLandmarkAsset(seaportLandmarkAsset.getLoginId(), seaportLandmarkAsset.getLandMarkId());
		return status;
	}
	//CR428.en
	
}
