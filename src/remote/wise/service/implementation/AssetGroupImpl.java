package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessentity.CustomAssetGroupEntity;
import remote.wise.businessobject.AssetCustomGroupDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AssetGroupReqContract;
import remote.wise.service.datacontract.AssetGroupRespContract;
//import remote.wise.util.WiseLogger;

/** Implementation class to handle CustomAssetGroup 
 * @author Rajani Nagaraju
 *
 */
public class AssetGroupImpl 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("AssetGroupImpl:","businessError");
	
	//****************************To insert / update asset group details**********************
	/** This method sets the details of Custom Asset group
	 * @param reqObj Details of CustomAssetGroup is specified through this reqObj
	 * @return Returns the CustomAssetGroupId as part of AssetGroupReqContract
	 * @throws CustomFault custom Exception is thrown if loginId/CustomAssetGroupName/TenancyId/ClientId is not specified/Invalid
	 */
	public AssetGroupReqContract setAssetGroup(AssetGroupRespContract reqObj) throws CustomFault
	{
	
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		AssetGroupReqContract resp = new AssetGroupReqContract();
		
		try
		{
			if(reqObj.getLoginId()==null)
			{
				throw new CustomFault("Login ID is NULL");
			}
			
			if(reqObj.getAssetGroupName()== null)
			{
				throw new CustomFault("Machine Group Name cannot be NULL");
			}
			if(reqObj.getTenancyId()==0)
			{
				throw new CustomFault("Tenancy is not sent");
			}
			
			if(reqObj.getClientId()==0)
			{
				throw new CustomFault("Client Id should be specified");
			}
			
			AssetCustomGroupDetailsBO  asset_group_BO = new AssetCustomGroupDetailsBO();
			int assetGroupId = asset_group_BO.setAssetGroup(reqObj.getLoginId(), reqObj.getAssetGroupId(), reqObj.getAssetGroupName(), reqObj.getAssetGroupDescription(),
											reqObj.getAssetGroupTypeId(), reqObj.getSerialNumberList(), reqObj.getTenancyId(), reqObj.getClientId());
			resp.setAssetGroupId(assetGroupId);
			if(assetGroupId == -1){
				
				resp.setMessage("Machine Group with the given details already exists. Please create different machine group");
			}
			else{
				
				resp.setMessage("Successfully added");
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e);
		}
		
		return resp;
	}
	
	
	
	//*******************************To get asset group details*****************************
	/** This method returns the details of specified customAssetGroup OR all customAssetGroups of the specified tenancy
	 * @param reqObj Specific customAssetGroup OR tenancy is specified through this reqObj
	 * @return Returns the details of required customAssetGroup
	 * @throws CustomFault customException is thrown if tenancyId is not specified
	 */
	public ArrayList<AssetGroupRespContract> getAssetGroup(AssetGroupReqContract reqObj) throws CustomFault
	{
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		AssetCustomGroupDetailsBO assetGroupBo = new AssetCustomGroupDetailsBO();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		//to get the asset Group details TenancyIdList is a mandatory parameter. Since the CustomerCare can select the customerTenancy and get 
		//the Machine Group under the customerTenancy, Machine Group details to be displayed is always with respect to the tenancy and not loginId
		try
		{
			if(reqObj.getLoginId()==null)
			{
				throw new CustomFault("Please provide LoginId");
			}
			if( (reqObj.getTenancyIdList()==null) || (reqObj.getTenancyIdList().isEmpty()) )
			{
				throw new CustomFault("Machine Group Details cannot be displayed if TenancyId is not passed");
			}
		}
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		ArrayList<AssetGroupRespContract> responseObjArraylist = new ArrayList<AssetGroupRespContract>();
		List<AssetCustomGroupDetailsBO> assetGroupBoObj =assetGroupBo.getAssetGroup(reqObj.getLoginId(), reqObj.getAssetGroupTypeId(), 
																	reqObj.getAssetGroupId(), reqObj.getSerialNumberList(), reqObj.getTenancyIdList(),reqObj.isOtherTenancy());
			
		for(int i=0; i<assetGroupBoObj.size(); i++)
		{
			AssetGroupRespContract response = new AssetGroupRespContract();
			response.setLoginId(assetGroupBoObj.get(i).getLoginId());
			response.setAssetGroupId(assetGroupBoObj.get(i).getAssetGroupId());
			response.setAssetGroupName(assetGroupBoObj.get(i).getAssetGroupName());
			response.setAssetGroupDescription(assetGroupBoObj.get(i).getAssetGroupDescription());
			response.setAssetGroupTypeId(assetGroupBoObj.get(i).getAssetGroupTypeId());
			response.setAssetGroupTypeName(assetGroupBoObj.get(i).getAssetGroupTypeName());
			response.setSerialNumberList(assetGroupBoObj.get(i).getSerialNumberList());
			response.setTenancyId(assetGroupBoObj.get(i).getTenancyId());
				
			responseObjArraylist.add(response);
		}
		return responseObjArraylist;
		
		
	}
	
	
	//*******************************End of get asset group details*****************************
	
	
	//**************************************************** Delete CustomAsset Group **********************************************
	/** This method deletes the specified customAssetGroup
	 * @param reqObj CustomAssetGroupId to be deleted is specified through this reqObj
	 * @return Returns the deletion status of specified CustomAssetGroup
	 * @throws CustomFault customException is thrown if assetGroupId is not specified
	 */
	public String deleteAssetGroup(AssetGroupRespContract reqObj) throws CustomFault
	{
		String status ="SUCCESS";
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		try
		{
			if(reqObj.getAssetGroupId()==0)
			{
				throw new CustomFault("Provide the Group Id to delete");
			}
			
			status=new AssetCustomGroupDetailsBO().deleteAssetGroup(reqObj.getAssetGroupId());
		}
		catch(CustomFault e)
		{
			status = "FAILURE";
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		return status;
	}
	
	//**************************************************** END of Delete CustomAsset Group **********************************************
}
