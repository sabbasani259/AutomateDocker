package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.DealerMappingService.DealerMappingService;
import remote.wise.EAintegration.clientPackage.DealerMappingService.DealerMappingServiceService;
import remote.wise.EAintegration.dataContract.DealerMappingInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class DealerMappingClient 
{
	public String invokeDealerMappingService(DealerMappingInputContract reqObj) 
	{
		//creating WebService Client
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		DealerMappingServiceService service1=null;
		DealerMappingService port1=null;
		
		try
		{
			iLogger.info(reqObj.getLlCode()+" : Get the wsdl location");
			service1 = new DealerMappingServiceService();
	
			//Create WebService
			
			iLogger.info(reqObj.getLlCode()+" : Get the webservice port");
			port1 = service1.getDealerMappingServicePort();
	     
			//Call webservice method
			iLogger.info(reqObj.getLlCode()+" :Invoke webservice");
			iLogger.info("EccCode:"+reqObj.getEccCode()+", CrmCode:"+reqObj.getCrmCode()+", LlCode:"+reqObj.getLlCode()+", DealerName:"+reqObj.getDealerName() +
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+", RJobCode:"+reqObj.getReprocessJobCode());
			
			status = port1.setDealerMapping(reqObj.getEccCode(), reqObj.getCrmCode(), reqObj.getLlCode(), reqObj.getDealerName(),
							reqObj.getMessageId(), reqObj.getFileRef(), reqObj.getProcess(), reqObj.getReprocessJobCode());
			iLogger.info(reqObj.getLlCode()+" :Returned from webservice, Status: "+status);
		}

		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-DealerMappingService:WSDL not available";
			fLogger.fatal(reqObj.getLlCode()+"DealerMappingService :WSDL not available");
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if(reqObj.getEccCode()==null)
				reqObj.setEccCode("");
			
			if(reqObj.getCrmCode()==null)
				reqObj.setCrmCode("");
		
			if(reqObj.getLlCode()==null)
				reqObj.setLlCode("");
			
			if(reqObj.getDealerName()==null)
				reqObj.setDealerName("");
		
			String messageString = reqObj.getEccCode()+"|"+reqObj.getCrmCode()+"|"+reqObj.getLlCode()+"|"+reqObj.getDealerName();
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(), "DealerMappingService WSDL not available");
		}
		
		return status;
	}
}
