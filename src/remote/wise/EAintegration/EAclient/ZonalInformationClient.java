package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.ZonalInformationService.ZonalInformationService;
import remote.wise.EAintegration.clientPackage.ZonalInformationService.ZonalInformationServiceService;
import remote.wise.EAintegration.dataContract.ZonalInformationInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;


public class ZonalInformationClient 
{
	public String invokeZonalInformation(ZonalInformationInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		ZonalInformationServiceService service1 =null;
		ZonalInformationService port1=null;
		
		try
		{
		
			//creating WebService Client
			iLogger.info(reqObj.getZonalCode()+" : Get the wsdl location");
			service1 = new ZonalInformationServiceService();
			
			//Create WebService
			iLogger.info(reqObj.getZonalCode()+" : Get the webservice port");
			port1 = service1.getZonalInformationServicePort();
			
			//Call webservice method
			iLogger.info(reqObj.getZonalCode()+" :Invoke webservice");
			iLogger.info("zonal Code:"+reqObj.getZonalCode()+", zonal name:"+reqObj.getZonalName() +				
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+"" +
					" , RJobCode:"+reqObj.getReprocessJobCode());
			status = port1.setZonalDetails(reqObj.getZonalName(), reqObj.getZonalCode(), 
								 reqObj.getMessageId(), reqObj.getFileRef(), reqObj.getProcess(), 
								reqObj.getReprocessJobCode());
			iLogger.info(reqObj.getZonalCode()+" :Returned from webservice, Status: "+status);
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-ZonalInformationService:WSDL not available";
			fLogger.fatal(reqObj.getZonalCode()+"ZonalInformationService:WSDL not available");
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if( reqObj.getZonalName() == null || reqObj.getZonalName().equals("") || (!(reqObj.getZonalName().replaceAll("\\s","").length()>0)) ){
				reqObj.setZonalName("");
			}				
			if( reqObj.getZonalCode() == null || reqObj.getZonalCode().equals("") || (!(reqObj.getZonalCode().replaceAll("\\s","").length()>0)) ){
				reqObj.setZonalCode("");
			}
			String messageString = reqObj.getZonalName()+"|"+reqObj.getZonalCode();

			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"ZonalInformationService WSDL not available");
		}
		return status;
	}
}

