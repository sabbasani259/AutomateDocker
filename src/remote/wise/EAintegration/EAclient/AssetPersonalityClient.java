package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.AssetPersonalityService.AssetPersonalityService;
import remote.wise.EAintegration.clientPackage.AssetPersonalityService.AssetPersonalityServiceService;
import remote.wise.EAintegration.dataContract.AssetPersonalityInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class AssetPersonalityClient 
{
	public String invokeAssetPersonalityService(AssetPersonalityInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		AssetPersonalityServiceService service1=null;
		AssetPersonalityService port1=null;
		
		try
		{
			//creating WebService Client
			iLogger.info(reqObj.getEngineNumber()+" : Get the wsdl location");
			service1 = new AssetPersonalityServiceService();
			
			//Create WebService
			iLogger.info(reqObj.getEngineNumber()+" : Get the webservice port");
			port1 = service1.getAssetPersonalityServicePort();
			
			//Call webservice method
			iLogger.info(reqObj.getEngineNumber()+" :Invoke webservice");
			iLogger.info("engineNumber:"+reqObj.getEngineNumber()+", AssetGroupCode:"+reqObj.getAssetGroupCode()+"" +
					", assetTypeCode:"+reqObj.getAssetTypeCode()+", engineTypeCode:"+reqObj.getEngineTypeCode()+"" +
					", assetBuiltDate:"+reqObj.getAssetBuiltDate()+", make:"+reqObj.getMake()+"" +
					", fuelCapacity:"+reqObj.getFuelCapacity()+", serialNumber:"+reqObj.getSerialNumber()+"" +
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+"" +
					" , RJobCode:"+reqObj.getReprocessJobCode());
			status = port1.setAssetPersonalityDetails(reqObj.getEngineNumber(), reqObj.getAssetGroupCode(), 
					reqObj.getAssetTypeCode(), reqObj.getEngineTypeCode(), 
					reqObj.getAssetBuiltDate(), reqObj.getMake(), reqObj.getFuelCapacity(), reqObj.getSerialNumber(), 
					reqObj.getMessageId(), reqObj.getFileRef(), reqObj.getProcess(), reqObj.getReprocessJobCode());
			iLogger.info(reqObj.getEngineNumber()+" :Returned from webservice, Status: "+status);
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-AssetPersonality :WSDL not available";
			fLogger.fatal(reqObj.getEngineNumber()+"AssetPersonality:WSDL not available");
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null)
		{
			if(reqObj.getEngineNumber()==null)
				reqObj.setEngineNumber("");
			 if(reqObj.getAssetGroupCode()==null)
				 reqObj.setAssetGroupCode("");
			 if(reqObj.getAssetTypeCode()==null)
				 reqObj.setAssetTypeCode("");
			 if(reqObj.getEngineTypeCode()==null)
				 reqObj.setEngineTypeCode("");
			 if(reqObj.getFuelCapacity()==null)
				 reqObj.setFuelCapacity("");
			 if(reqObj.getAssetBuiltDate()==null)
				 reqObj.setAssetBuiltDate("");
			 if(reqObj.getSerialNumber()==null)
				 reqObj.setSerialNumber("");
			 
			String messageString = reqObj.getEngineNumber()+"|"+reqObj.getAssetGroupCode()+"|"+reqObj.getAssetTypeCode()+"|"+
								reqObj.getEngineTypeCode()+"|"+reqObj.getAssetBuiltDate()+"|"+reqObj.getMake()+"|"+
								reqObj.getFuelCapacity()+"|"+reqObj.getSerialNumber();
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			/*errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"AssetPersonality WSDL not available");*/
			errorHandler.handleErrorMessages_new(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"AssetPersonality WSDL not available","0002","Service Invokation");
			
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(reqObj.getMessageId().split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(reqObj.getFileRef(), "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}
		}
		else{
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
