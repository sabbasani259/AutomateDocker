package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetTypeImpl;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;

/** Webservice to obtain the asset Type details (Model) from EA system
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "AssetTypeService")
public class AssetTypeService 
{
	
	/** Web Method to capture Asset Type details received from EA system
	 * @param assetTypeName Model Name
	 * @param assetTypeCode Model Code
	 * @param messageId
	 * @param fileRef
	 * @param process
	 * @param reprocessJobCode
	 * @return
	 */
	@WebMethod(operationName = "setAssetTypeDetails", action = "setAssetTypeDetails")
	public String setAssetTypeDetails(@WebParam(name="assetTypeName" ) String assetTypeName, 
			 			  @WebParam(name="assetTypeCode" ) String assetTypeCode,
			 			  @WebParam(name="messageId" ) String messageId,
			 			  @WebParam(name="fileRef" ) String fileRef,
			 			  @WebParam(name="process" ) String process,
			 			  @WebParam(name="reprocessJobCode" ) String reprocessJobCode)
	{
		String response ="SUCCESS-Record Processed";
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetTypeService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("EA Processing: AssetTypeDetails: "+messageId+": ---- Actual Webservice Input ------");
		iLogger.info("EA Processing: AssetTypeDetails: "+messageId+": Asset Type Name:"+assetTypeName+",  "+"Asset Type Code:"+assetTypeCode +
				",  "+"Message Id:"+messageId+",  "+"File Reference:"+fileRef+
				",  "+"Process:"+process+",  "+"Reprocess JobCode:"+reprocessJobCode);
		
		long startTime = System.currentTimeMillis();
		response = new AssetTypeImpl().setAssetType(assetTypeName,assetTypeCode, messageId);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:AssetTypeService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		iLogger.info("EA Processing: AssetTypeDetails: "+messageId+": ----- Actual Webservice Output-----");
		iLogger.info("EA Processing: AssetTypeDetails: "+messageId+": Status:"+response);
		
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
			if(assetTypeName==null)
				assetTypeName="";
			if(assetTypeCode==null)
				assetTypeCode="";
			String messageString = assetTypeName+"|"+assetTypeCode;
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
