package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.PrimaryDealerTransfer.PrimaryDealerTransferService;
import remote.wise.EAintegration.clientPackage.PrimaryDealerTransfer.PrimaryDealerTransferServiceService;
import remote.wise.EAintegration.dataContract.PrimaryDealerTransferInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;


public class PrimaryDealerTransferClient 
{
	public String invokePrimaryDealerTransfer(PrimaryDealerTransferInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		PrimaryDealerTransferServiceService service1=null;
		PrimaryDealerTransferService port1=null;
		
		try
		{
		
			//creating WebService Client
			iLogger.info(reqObj.getCustomerCode()+" : Get the wsdl location");
			service1 = new PrimaryDealerTransferServiceService();
			
			//Create WebService
			iLogger.info(reqObj.getCustomerCode()+" : Get the webservice port");
			port1 = service1.getPrimaryDealerTransferServicePort();
			
			//Call webservice method
			iLogger.info(reqObj.getCustomerCode()+" :Invoke webservice");
			iLogger.info("Customer Code:"+reqObj.getCustomerCode()+", Dealer Code:"+reqObj.getDealerCode()+"" +
					", Transfer Date:"+reqObj.getTransferDate()+"" +" , Serial Number:"+reqObj.getSerialNumber()+
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+"" +
					" , RJobCode:"+reqObj.getReprocessJobCode());
			status = port1.primaryDealerTransfer(reqObj.getCustomerCode(), reqObj.getDealerCode(), 
										reqObj.getTransferDate(), reqObj.getSerialNumber(),reqObj.getMessageId(), 
										reqObj.getFileRef(), reqObj.getProcess(), reqObj.getReprocessJobCode());
			iLogger.info(reqObj.getSerialNumber()+" :Returned from webservice, Status: "+status);
		}
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-PrimaryDealerTransferService:WSDL not available";
			fLogger.fatal(reqObj.getSerialNumber()+"PrimaryDealerTransferService:WSDL not available");
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if(reqObj.getCustomerCode()==null)
				reqObj.setCustomerCode("");
			if(reqObj.getDealerCode()==null)
				reqObj.setDealerCode("");
			if(reqObj.getTransferDate()==null)
				reqObj.setTransferDate("");
			if(reqObj.getSerialNumber()==null)
				reqObj.setSerialNumber("");
			 
			String messageString = reqObj.getCustomerCode()+"|"+reqObj.getDealerCode()+"|"+reqObj.getTransferDate()+"|"+reqObj.getSerialNumber();
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"PrimaryDealerTransferService WSDL not available");
		}
		
		return status;
	}
}
