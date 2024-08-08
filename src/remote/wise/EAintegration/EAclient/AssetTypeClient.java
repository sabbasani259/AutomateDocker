package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.AssetTypeService.AssetTypeService;
import remote.wise.EAintegration.clientPackage.AssetTypeService.AssetTypeServiceService;
import remote.wise.EAintegration.dataContract.AssetTypeInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;



public class AssetTypeClient 
{
	public String invokeAssetTypeService(AssetTypeInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		AssetTypeServiceService service1=null; 
		AssetTypeService port1=null;
		
		try
		{
			//creating WebService Client
			iLogger.info(reqObj.getAssetTypeName()+" : Get the wsdl location");
			 service1 = new AssetTypeServiceService();
			
			//Create WebService
			iLogger.info(reqObj.getAssetTypeName()+" : Get the webservice port");
			port1 = service1.getAssetTypeServicePort();
			
			//Call webservice method
			iLogger.info(reqObj.getAssetTypeName()+" :Invoke webservice");
			iLogger.info("AssetTypeName:"+reqObj.getAssetTypeName()+", AssetTypecode:"+reqObj.getAssetTypeCode()+"" +
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+", RJobCode:"+reqObj.getReprocessJobCode());
			status = port1.setAssetTypeDetails(reqObj.getAssetTypeName(), reqObj.getAssetTypeCode(), 
					reqObj.getMessageId(), reqObj.getFileRef(), reqObj.getProcess(), reqObj.getReprocessJobCode());
			iLogger.info(reqObj.getAssetTypeName()+" :Returned from webservice, Status: "+status);
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-AssetTypeService:WSDL not available";
			fLogger.fatal(reqObj.getAssetTypeName()+"AssetTypeService :WSDL not available");
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if(reqObj.getAssetTypeName()==null)
				reqObj.setAssetTypeName("");
			if(reqObj.getAssetTypeCode()==null)
				reqObj.setAssetTypeCode("");
			String messageString = reqObj.getAssetTypeName()+"|"+reqObj.getAssetTypeCode();
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			/*errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(), "AssetTypeService WSDL not available");*/
			errorHandler.handleErrorMessages_new(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(), "AssetTypeService WSDL not available","0002","Service Invokation");
			
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(reqObj.getMessageId().split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(reqObj.getFileRef(), "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}
		}else{
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			String uStatus = null;
			if(reqObj.getMessageId().split("\\|").length<2){
				 uStatus = CommonUtil.updateInterfaceLogDetails(reqObj.getFileRef(), "successfullServiceInvocation", 1);
				 iLogger.info("Status on updating data into interface log details table :"+uStatus);
			 } 
		}
		
		return status;
	}
}
