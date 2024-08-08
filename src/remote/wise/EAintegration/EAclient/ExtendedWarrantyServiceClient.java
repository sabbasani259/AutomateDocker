package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.JcbRollOffService.CustomFault;
import remote.wise.EAintegration.dataContract.ExtendedWarrantyServiceInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.webservice.ExtendedWarrentyRESTService;
import remote.wise.util.CommonUtil;

public class ExtendedWarrantyServiceClient {

	public String extendedWarrantyService(ExtendedWarrantyServiceInputContract reqObj) throws CustomFault
	{

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		ExtendedWarrentyRESTService serviceObj = new ExtendedWarrentyRESTService();
		
		try
		{
		
			iLogger.info("ExtendedWarrantyService - "+reqObj.getSerialNumber()+" :Invoke webservice");
			iLogger.info("ExtendedWarrantyService- processExtendedWarrantyServiceData Input to the RESTservice::"+reqObj.getSerialNumber()+","+reqObj.getCallTypeId()+","+reqObj.getFileRef()+","+"" +
					reqObj.getMessageId()+","+reqObj.getProcess()+","+reqObj.getReprocessJobCode()+","+reqObj.getSerialNumber()+","+"" +
					reqObj.getMonthlyVisit()+","+reqObj.getCancellationFlag());
			
			status = serviceObj.updateExtendedWarrantyDetails(reqObj.getSerialNumber(), reqObj.getCallTypeId(),reqObj.getMonthlyVisit()
					,reqObj.getCancellationFlag(),
					 reqObj.getMessageId(),reqObj.getFileRef(), reqObj.getProcess(),reqObj.getReprocessJobCode());
			
			iLogger.info("Service History - "+reqObj.getSerialNumber()+" :Returned from webservice, Status: "+status);
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-ExtendedWarrentyService:RESTService not available";
			fLogger.fatal("ExtendedWarrentyService - "+reqObj.getSerialNumber()+"ExtendedWarrentyService:RESTService not available");
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 /*|| port1==null || service1==null*/)
		{
			if(reqObj.getSerialNumber()==null)
				reqObj.setSerialNumber("");
			if(reqObj.getCallTypeId()==null)
				reqObj.setCallTypeId("");
			if(reqObj.getMonthlyVisit()==null)
				reqObj.setMonthlyVisit("");
			if(reqObj.getCancellationFlag()==null)
				reqObj.setCancellationFlag("");
			if(reqObj.getMessageId()==null)
				reqObj.setMessageId("");
			if(reqObj.getFileRef()==null)
				reqObj.setFileRef("");
			if(reqObj.getReprocessJobCode()==null)
				reqObj.setReprocessJobCode("");
			
			String messageString = reqObj.getSerialNumber()+"|"+reqObj.getCallTypeId()+"|"+reqObj.getMonthlyVisit()+"|"+
								reqObj.getCancellationFlag()+"|"+reqObj.getMessageId()+"|"+reqObj.getFileRef()+"|"+reqObj.getReprocessJobCode();
			
			fLogger.fatal("ExtendedWarrentyService - "+messageString+" Exception:: Put the record into fault details");
			
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			/*errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"ServiceHistoryDetailsService WSDL not available");*/
			errorHandler.handleErrorMessages_new(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"ExtendedWarrentyService RESTService not available","0002","Service Invokation");
			
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(reqObj.getMessageId()!=null && reqObj.getMessageId().split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(reqObj.getFileRef(), "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}
		}
		
		else{
			//DF20180207:KO369761 - updating datacount to log details table for tracing.
			String uStatus = null;
			if(reqObj.getMessageId()!=null && reqObj.getMessageId().split("\\|").length<2){
				uStatus = CommonUtil.updateInterfaceLogDetails(reqObj.getFileRef(), "successfullServiceInvocation", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}
		}
		
		return status;
	
	}
}
