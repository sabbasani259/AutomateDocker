package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.AssetCustomGroupDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AssetGroupTypeReqContract;
import remote.wise.service.datacontract.AssetGroupTypeRespContract;
//import remote.wise.util.WiseLogger;

/** Implementation class for Custom Asset Group Type details
 * @author Rajani Nagaraju
 *
 */
public class AssetGroupTypeImpl 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("AssetGroupTypeImpl:","businessError");
	
	/** This method returns the details of the specified Custom Asset Group type
	 * @param reqObj Specifies the tenancy / specific asset group type 
	 * @return Returns the list Asset Group Type with their details
	 * @throws CustomFault
	 */
	public List<AssetGroupTypeRespContract> getAssetGroupType(AssetGroupTypeReqContract reqObj) throws CustomFault
	{
		List<AssetGroupTypeRespContract> responseList = new LinkedList<AssetGroupTypeRespContract>();
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		try
		{
			//LoginId - Mandatory parameter
			if(reqObj.getLoginId()== null)
			{
				throw new CustomFault("Please provide Login Id");
			}
			
			//tenancyIdList - Mandatory parameter - Either LoginUserTenancyId OR any other tenancy that can be selected by CC/Admin
			if(reqObj.getTenancyIdList()==null || reqObj.getTenancyIdList().isEmpty())
			{
				throw new CustomFault("Please provide tenancy Id to get the details");
			}
			
			
			List<AssetCustomGroupDetailsBO> assetGroupBoList = new AssetCustomGroupDetailsBO().getAssetGroupType(reqObj.getLoginId(),
															reqObj.getTenancyIdList(),reqObj.getAssetGroupTypeId(),reqObj.isOtherTenancy());
						
			for(int i=0; i<assetGroupBoList.size();i++)
			{
				AssetGroupTypeRespContract response = new AssetGroupTypeRespContract();
				response.setAssetGroupTypeId(assetGroupBoList.get(i).getAssetGroupId());
				response.setAssetGroupTypeName(assetGroupBoList.get(i).getAssetGroupName());
				response.setAssetGroupTypeDescription(assetGroupBoList.get(i).getAssetGroupDescription());
				response.setTenancyId(assetGroupBoList.get(i).getTenancyId());
				
				responseList.add(response);
			}
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault:"+e.getFaultInfo());
		}
		
		return responseList;
	} 
	
	
	/** This method sets the details of Custom Asset Group Type
	 * @param reqObj Details of Custom Asset Group type to set
	 * @return Returns the status string
	 * @throws CustomFault customException is thrown if the tenancyId/ClientId/groupTypeName is not specified
	 */
	public String setAssetGroupType(AssetGroupTypeRespContract reqObj) throws CustomFault
	{
		String status = "SUCCESS";
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
	
		try
		{
			//TenancyId - Mandatory parameter
			if(reqObj.getTenancyId()==0)
			{
				throw new CustomFault("Tenancy is not specified");
			}
				
			//ClientId - Mandatory parameter
			if(reqObj.getClientId()==0)
			{
				bLogger.error("ClientId is not specified");
				throw new CustomFault("ClientID is not specified");
			}
			
			//AssetGroupTypeName - Mandatory Parameter
			if(reqObj.getAssetGroupTypeName()==null)
			{
				bLogger.error("Machine Group Type Name is not specified");
				throw new CustomFault("Machine Group Type Name is not specified");
			}
			
			AssetCustomGroupDetailsBO customAssetGroupBo = new AssetCustomGroupDetailsBO();
			status = customAssetGroupBo.setAssetGroupType(reqObj.getTenancyId(),reqObj.getAssetGroupTypeId(), reqObj.getAssetGroupTypeName(),
																	reqObj.getAssetGroupTypeDescription(),reqObj.getClientId());
		}
		catch(CustomFault e)
		{
			status = "FAILURE";
			bLogger.error("Custom Fault:"+e.getFaultInfo());
			
		}
		
		return status;
		
	}
	
	
	/** This method deletes the custom Asset group type if no custom asset groups are mapped under it
	 * @param reqObj AssetGroupTypeId that has to be deleted
	 * @return returns the status String of AssetGroupType
	 * @throws CustomFault
	 */
	public String deleteAssetGroupType(AssetGroupTypeReqContract reqObj) throws CustomFault
	{
		String status ="SUCCESS";
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		try
		{
			if(reqObj.getAssetGroupTypeId()==0)
			{
				throw new CustomFault("CustomAssetGroupTypeId to be deleted is not specified");
			}
			AssetCustomGroupDetailsBO customAssetGroupBo = new AssetCustomGroupDetailsBO();
			status = customAssetGroupBo.deleteAssetGroupType(reqObj.getAssetGroupTypeId());
		}
		catch(CustomFault e)
		{
			status = "FAILURE";
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		return status;
	}
}
