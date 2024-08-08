package remote.wise.service.implementation;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
//import remote.wise.util.WiseLogger;

/** Implementation class to capture the Profile Details as Master data from EA system
 * @author Rajani Nagaraju
 *
 */
public class AssetProfileImpl 
{
	//public static WiseLogger businessError = WiseLogger.getLogger("AssetProfileImpl:","businessError");
	
	public String setAssetProfile(String assetProfileName, String assetProfileCode, String messageId)
	{
		String status = "SUCCESS-Record Processed";
		AssetDetailsBO assetDetailsBo = new AssetDetailsBO();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		
		//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
		if(assetProfileName==null || assetProfileName.trim()==null || assetProfileName.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter Asset Profile Name is NULL";
			bLogger.error("EA Processing: AssetGroupDetails: "+messageId+" : Mandatory Parameter Asset Profile Name is NULL");
			return status;
		}
		
		if(assetProfileCode==null || assetProfileCode.trim()==null ||  assetProfileCode.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter Asset Profile Code is NULL";
			bLogger.error("EA Processing: AssetGroupDetails: "+messageId+" : Mandatory Parameter Asset Profile Code is NULL");
			return status;
		}
		
			
		status = assetDetailsBo.setAssetProfile(assetProfileName,assetProfileCode,messageId);
		
		return status;
	}
}
