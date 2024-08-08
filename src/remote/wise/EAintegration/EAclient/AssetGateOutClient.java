package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.AssetGateOut.AssetGateoutService;
import remote.wise.EAintegration.clientPackage.AssetGateOut.AssetGateoutServiceService;
import remote.wise.EAintegration.dataContract.AssetGateOutInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;


public class AssetGateOutClient 
{
	public String invokeAssetGateOut(AssetGateOutInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		AssetGateoutServiceService service1=null;
		AssetGateoutService port1=null;
		
		try
		{
			//Logger infoLogger = Logger.getLogger("infoLogger"); 
			
			//creating WebService Client
			iLogger.info("Asset GateOut - "+reqObj.getEngineNumber()+" : Get the wsdl location");
			service1 = new AssetGateoutServiceService();
			
			//Create WebService
			iLogger.info("Asset GateOut - "+reqObj.getEngineNumber()+"Creating WebService - Asset Gate Out");
			port1 = service1.getAssetGateoutServicePort();
			
			//Call webservice method
			iLogger.info("Asset GateOut - "+reqObj.getEngineNumber()+" :Invoke webservice");
			//DF20190425 :: adding vin in the log to identify the interface lag issue, at vin level
			iLogger.info("Asset GateOut - "+reqObj.getSerialNumber()+" :Invoke webservice");
			if(reqObj.getFileRef()!=null)
			{
				iLogger.info("Asset GateOut - "+reqObj.getFileRef()+" :Invoke webservice");
			}
			iLogger.info("Asset GateOut - input"+"dealer Code:"+reqObj.getDealerCode()+", Customer Code:"+reqObj.getCustomerCode()+"" +
					", engineNumber:"+reqObj.getEngineNumber()+", serialNumber:"+reqObj.getSerialNumber()+"" +
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+"" +
					" , RJobCode:"+reqObj.getReprocessJobCode());
			status = port1.setAssetGateoutService(reqObj.getDealerCode(), reqObj.getCustomerCode(), reqObj.getEngineNumber(), 
								reqObj.getSerialNumber(), reqObj.getMessageId(), reqObj.getFileRef(), reqObj.getProcess(), 
								reqObj.getReprocessJobCode());
			iLogger.info("Asset GateOut - "+reqObj.getEngineNumber()+" :Returned from webservice, Status: "+status);
			//DF20190425 :: adding vin in the log to identify the interface lag issue, at vin level
			iLogger.info("Asset GateOut - "+reqObj.getSerialNumber()+" :Returned from webservice, Status: "+status);
			if(reqObj.getFileRef()!=null)
			{
				iLogger.info("Asset GateOut - "+reqObj.getFileRef()+" :Returned from webservice, Status: "+status);
			}

		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-AssetGateout:WSDL not available";
			fLogger.fatal("Asset GateOut - "+reqObj.getEngineNumber()+"AssetGateout:WSDL not available");
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if(reqObj.getDealerCode()==null)
				reqObj.setDealerCode("");
			 if(reqObj.getCustomerCode()==null)
				 reqObj.setCustomerCode("");
			 if(reqObj.getEngineNumber()==null)
				 reqObj.setEngineNumber("");
			 if(reqObj.getSerialNumber()==null)
				 reqObj.setSerialNumber("");
			 
			String messageString = reqObj.getDealerCode()+"|"+reqObj.getCustomerCode()+"|"+reqObj.getEngineNumber()+"|"+reqObj.getSerialNumber();
			
			fLogger.fatal("Asset GateOut - "+messageString+" Exception:: Put the record into fault details");
			
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			/*errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(), "AssetGateoutService WSDL not available");*/
			errorHandler.handleErrorMessages_new(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(),reqObj.getReprocessJobCode(), "AssetGateoutService WSDL not available","0002","Service Invokation");
			
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
