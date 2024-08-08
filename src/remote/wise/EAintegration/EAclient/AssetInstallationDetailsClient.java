package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.AssetInstallationDetails.InstallationDateDetailsService;
import remote.wise.EAintegration.clientPackage.AssetInstallationDetails.InstallationDateDetailsServiceService;
import remote.wise.EAintegration.dataContract.AssetInstallationDetailsInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class AssetInstallationDetailsClient 
{
	public String invokeAssetInstallationService(AssetInstallationDetailsInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		InstallationDateDetailsService port1=null;
		InstallationDateDetailsServiceService service1=null;
		
		try
		{
			//creating WebService Client
			iLogger.info("Asset Installation - "+reqObj.getSerialNumber()+" : Get the wsdl location");
			service1 = new InstallationDateDetailsServiceService();
			
			//Create WebService
			iLogger.info("Asset Installation - "+reqObj.getSerialNumber()+" : Get the webservice port");
			port1 = service1.getInstallationDateDetailsServicePort();
			
			//Call webservice method
			iLogger.info("Asset Installation - "+reqObj.getSerialNumber()+" :Invoke webservice");
			iLogger.info("Asset Installation - input"+"Serial Number:"+reqObj.getSerialNumber()+",  "+"Installation Date:"+reqObj.getInstallationDate()+",   " +
					"DealerCode: "+reqObj.getDealerCode()+",  "+"Customer Code: "+reqObj.getCustomerCode() +
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+"" +
					" , RJobCode:"+reqObj.getReprocessJobCode());
			
			status = port1.setAssetServiceSchedule(reqObj.getSerialNumber(), reqObj.getInstallationDate(), 
					reqObj.getDealerCode(), reqObj.getCustomerCode(), reqObj.getMessageId(), reqObj.getFileRef(), 
					reqObj.getProcess(), reqObj.getReprocessJobCode());
			iLogger.info("Asset Installation - "+reqObj.getSerialNumber()+" :Returned from webservice, Status: "+status);
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-InstallationDateDetailService:WSDL not available";
			fLogger.fatal("Asset Installation - "+reqObj.getSerialNumber()+"InstallationDateDetailService :WSDL not available");
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if(reqObj.getSerialNumber()==null)
				reqObj.setSerialNumber("");
			 if(reqObj.getInstallationDate()==null)
				 reqObj.setInstallationDate("");
			 if(reqObj.getDealerCode()==null)
				 reqObj.setDealerCode("");
			 if(reqObj.getCustomerCode()==null)
				 reqObj.setCustomerCode("");
			 				 
			String messageString = reqObj.getSerialNumber()+"|"+reqObj.getInstallationDate()+"|"+reqObj.getDealerCode()+"|"+reqObj.getCustomerCode();
			
			fLogger.fatal("Asset Installation - "+messageString+" Exception:: Put the record into fault details");
			
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			/*errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(), "InstallationDateDetailService WSDL not available");*/
			errorHandler.handleErrorMessages_new(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(), "InstallationDateDetailService WSDL not available","0002","Service Invokation");
			
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
