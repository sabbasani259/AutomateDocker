package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.MasterServiceSchedule.MasterServiceSchedule;
import remote.wise.EAintegration.clientPackage.MasterServiceSchedule.MasterServiceScheduleService;
import remote.wise.EAintegration.dataContract.MasterServiceScheduleInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class MasterServiceScheduleClient 
{
	public String invokeMasterServiceSchedule(MasterServiceScheduleInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		MasterServiceScheduleService service1=null;
		MasterServiceSchedule port1=null;
		
		try
		{
		
			//creating WebService Client
			iLogger.info(reqObj.getServiceName()+" : Get the wsdl location");
			service1 = new MasterServiceScheduleService();
			
			//Create WebService
			iLogger.info(reqObj.getServiceName()+" : Get the webservice port");
			 port1 = service1.getMasterServiceSchedulePort();
			
			//Call webservice method
			 iLogger.info(reqObj.getServiceName()+" :Invoke webservice");
			iLogger.info("servicePlan:"+reqObj.getServicePlan()+",  "+"serviceName:"+reqObj.getServiceName()+",  " +
					"scheduleName:"+reqObj.getScheduleName()+",  "+"dbmsPartCode:"+reqObj.getDbmsPartCode()+",  " +
					"assetGroupCode:"+reqObj.getAssetGroupCode()+",  "+"assetTypeCode:"+reqObj.getAssetTypeCode()+",  " +
					"engineTypeCode:"+reqObj.getEngineTypeCode()+",  "+"engineHours:"+reqObj.getEngineHours()+",  " +"days:"+reqObj.getDays()+"" +
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+"" +
					" , RJobCode:"+reqObj.getReprocessJobCode());
			status = port1.setMasterServiceScheduleDetails(reqObj.getServicePlan(), reqObj.getServiceName(), reqObj.getScheduleName(), 
					reqObj.getDbmsPartCode(), reqObj.getAssetGroupCode(), reqObj.getAssetTypeCode(), reqObj.getEngineTypeCode(), 
					reqObj.getEngineHours(), reqObj.getDays(), reqObj.getMessageId(), reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode());
			
			iLogger.info(reqObj.getServiceName()+" :Returned from webservice, Status: "+status);
		}
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-MasterServiceSchedule :WSDL not available";
			fLogger.fatal(reqObj.getServiceName()+"MasterServiceSchedule:WSDL not available");
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if(reqObj.getServicePlan()==null)
				reqObj.setServicePlan("");
			if(reqObj.getServiceName()==null)
				reqObj.setServiceName("");
			 if(reqObj.getScheduleName()==null)
				 reqObj.setScheduleName("");
			 if(reqObj.getDbmsPartCode()==null)
				 reqObj.setDbmsPartCode("");
			 if(reqObj.getAssetGroupCode()==null)
				 reqObj.setAssetGroupCode("");
			 if(reqObj.getAssetTypeCode()==null)
				 reqObj.setAssetTypeCode("");
			 if(reqObj.getEngineTypeCode()==null)
				 reqObj.setEngineTypeCode("");
			 if(reqObj.getEngineHours()==null)
				 reqObj.setEngineHours("");
			 if(reqObj.getDays()==null)
				 reqObj.setDays("");
			 
			String messageString = reqObj.getServicePlan()+"|"+reqObj.getServiceName()+"|"+reqObj.getScheduleName()+"|"+
							reqObj.getDbmsPartCode()+"|"+reqObj.getAssetGroupCode()+"|"+reqObj.getAssetTypeCode()+"|"+
							reqObj.getEngineTypeCode()+"|"+reqObj.getEngineHours()+"|"+reqObj.getDays();
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"MasterServiceSchedule WSDL not available");
		}
		return status;
	}
}
