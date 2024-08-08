package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.EngineTypeService.EngineTypeService;
import remote.wise.EAintegration.clientPackage.EngineTypeService.EngineTypeServiceService;
import remote.wise.EAintegration.dataContract.EngineTypeInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;



public class EngineTypeClient 
{
	public String invokeEngineTypeService(EngineTypeInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		EngineTypeServiceService service1=null;
		EngineTypeService port1=null;
		
		try
		{
			//creating WebService Client
			iLogger.info(reqObj.getEngineTypeName()+" : Get the wsdl location");
			service1 = new EngineTypeServiceService();
			
			//Create WebService
			iLogger.info(reqObj.getEngineTypeName()+" : Get the webservice port");
			port1 = service1.getEngineTypeServicePort();
			
			//Call webservice method
			iLogger.info(reqObj.getEngineTypeName()+" :Invoke webservice");
			iLogger.info("EngineTypeName:"+reqObj.getEngineTypeName()+", EngineTypeCode:"+reqObj.getEngineTypeCode()+"" +
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+", RJobCode:"+reqObj.getReprocessJobCode());
			status = port1.setEngineTypeDetails(reqObj.getEngineTypeName(), reqObj.getEngineTypeCode(), 
					reqObj.getMessageId(), reqObj.getFileRef(), reqObj.getProcess(), reqObj.getReprocessJobCode());
			iLogger.info(reqObj.getEngineTypeName()+" :Returned from webservice, Status: "+status);
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-EngineTypeService :WSDL not available";
			fLogger.fatal(reqObj.getEngineTypeName()+"EngineTypeService:WSDL not available");
			
		}
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if(reqObj.getEngineTypeName()==null)
				reqObj.setEngineTypeName("");
			if(reqObj.getEngineTypeCode()==null)
				reqObj.setEngineTypeCode("");
			String messageString = reqObj.getEngineTypeName()+"|"+reqObj.getEngineTypeCode();
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"EngineTypeService WSDL not available");
		}
		
		return status;
	}
}
