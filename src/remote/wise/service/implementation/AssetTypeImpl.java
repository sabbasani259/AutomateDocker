package remote.wise.service.implementation;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
//import remote.wise.util.WiseLogger;

/** Implementation class to capture the Asset Type Details as Master data from EA system
 * @author Rajani Nagaraju
 *
 */
public class AssetTypeImpl 
{
	//public static WiseLogger businessError = WiseLogger.getLogger("AssetProfileImpl:","businessError");
	
	public String setAssetType(String assetTypeName, String assetTypeCode, String messageId)
	{
		String status = "SUCCESS-Record Processed";
		AssetDetailsBO assetDetailsBo = new AssetDetailsBO();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
				
		//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
		if(assetTypeName==null || assetTypeName.trim()==null || assetTypeName.replaceAll("\\s","") .length()==0)
		{
			status = "FAILURE-Mandatory Parameter Asset Type Name is NULL";
			bLogger.error("EA Processing: AssetTypeDetails: "+messageId+" : Mandatory Parameter Asset Type Name is NULL");
			return status;
		}
		
		if(assetTypeCode==null ||  assetTypeCode.trim()==null || assetTypeCode.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter Asset Type Code is NULL";
			bLogger.error("EA Processing: AssetTypeDetails: "+messageId+" : Mandatory Parameter Asset Type Code is NULL");
			return status;
		}
				
		
		status = assetDetailsBo.setAssetType(assetTypeName,assetTypeCode,messageId);
		
		return status;
	}
}
