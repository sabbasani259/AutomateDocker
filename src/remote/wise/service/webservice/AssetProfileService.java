package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetProfileImpl;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;

/** Webservice to obtain the asset profile details from EA system
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "AssetProfileService")
public class AssetProfileService 
{
	
	/** Web Method to capture Asset Profile details received from EA system
	 * @param assetGroupName  Asset Profile Name
	 * @param assetGroupCode  Asset Profile Code
	 * @param messageId
	 * @param fileRef
	 * @param process
	 * @param reprocessJobCode
	 * @return
	 */
	@WebMethod(operationName = "setAssetProfileDetails", action = "setAssetProfileDetails")
	public String setAssetProfileDetails(@WebParam(name="assetGroupName" ) String assetGroupName, 
			 			  @WebParam(name="assetGroupCode" ) String assetGroupCode,
			 			  @WebParam(name="messageId" ) String messageId,
			 			  @WebParam(name="fileRef" ) String fileRef,
			 			  @WebParam(name="process" ) String process,
			 			  @WebParam(name="reprocessJobCode" ) String reprocessJobCode)
	{
		String response ="SUCCESS-Record Processed";
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetProfileService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("EA Processing: AssetGroupDetails: "+messageId+": ---- Actual Webservice Input ------");
		iLogger.info("EA Processing: AssetGroupDetails: "+messageId+": Asset Group Name:"+assetGroupName+",  "+"Asset Group Code:"+assetGroupCode +
				",  "+"Message Id:"+messageId+",  "+"File Reference:"+fileRef+
				",  "+"Process:"+process+",  "+"Reprocess JobCode:"+reprocessJobCode);
		
		long startTime = System.currentTimeMillis();
		response = new AssetProfileImpl().setAssetProfile(assetGroupName,assetGroupCode,messageId);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:AssetProfileService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		
		iLogger.info("EA Processing: AssetGroupDetails: "+messageId+": ----- Actual Webservice Output-----");
		iLogger.info("EA Processing: AssetGroupDetails: "+messageId+": Status:"+response);
		
		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
		String faultCause =null;
		if(response.split("-").length>1)
		{
			faultCause=response.split("-")[1];
			response = response.split("-")[0].trim();
					
		}
		
		//If Failure in insertion, put the message to fault_details table
		if(response.equalsIgnoreCase("FAILURE"))
		{
			 if(assetGroupName==null)
				assetGroupName="";
			 if(assetGroupCode==null)
				 assetGroupCode="";
			String messageString = assetGroupName+"|"+assetGroupCode;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode, faultCause);
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, reprocessJobCode, faultCause,"0003","Service Layer");
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}
		}else{
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "sucessCount", 1);
			iLogger.info("Status on updating data into interface log details table :"+uStatus);
			if(messageId!=null && messageId.split("\\|").length>1){
				CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", -1);
			}
			//DF20180207:KO369761 - deleting message from fault details table if it is there.
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			messageHandlerObj.deleteErrorMessage(messageId);
		}
		
		return response;
		
	}
}
