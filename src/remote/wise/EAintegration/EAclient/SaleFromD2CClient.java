package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.AssetSaleFromD2C.AssetSaleFromD2Cservice;
import remote.wise.EAintegration.clientPackage.AssetSaleFromD2C.AssetSaleFromD2CserviceService;
import remote.wise.EAintegration.dataContract.SaleFromD2CInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class SaleFromD2CClient 
{
	public String invokeSaleFromD2CService(SaleFromD2CInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		AssetSaleFromD2CserviceService service1=null;
		AssetSaleFromD2Cservice port1=null;
		
		try
		{
		
			//creating WebService Client
			iLogger.info(reqObj.getSerialNumber()+" : Get the wsdl location");
			service1 = new AssetSaleFromD2CserviceService();
			
			//Create WebService
			iLogger.info(reqObj.getSerialNumber()+" : Get the webservice port");
			port1 = service1.getAssetSaleFromD2CservicePort();
			
			//Call webservice method
			iLogger.info(reqObj.getSerialNumber()+" :Invoke webservice");
			iLogger.info("seller Code:"+reqObj.getSellerCode()+", Buyer Code:"+reqObj.getBuyerCode()+", Dealer Code:"+reqObj.getDealerCode()+"" +
					", Serial Number:"+reqObj.getSerialNumber()+", Transfer Date:"+reqObj.getTransferDate()+"" +
					" , MessageId:"+reqObj.getMessageId()+", fileRef:"+reqObj.getFileRef()+", Process:"+reqObj.getProcess()+"" +
					" , RJobCode:"+reqObj.getReprocessJobCode());
			status = port1.assetSaleFromDealerToCust(reqObj.getSellerCode(), reqObj.getBuyerCode(), reqObj.getDealerCode(),
					reqObj.getSerialNumber(), reqObj.getTransferDate(), reqObj.getMessageId(), 
					reqObj.getFileRef(), reqObj.getProcess(), reqObj.getReprocessJobCode());
			iLogger.info(reqObj.getSerialNumber()+" :Returned from webservice, Status: "+status);
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-AssetSaleFromD2Cservice:WSDL not available";
			fLogger.fatal(reqObj.getSerialNumber()+"AssetSaleFromD2Cservice: WSDL not available");
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if(reqObj.getSellerCode()==null)
				reqObj.setSellerCode("");
			if(reqObj.getBuyerCode()==null)
				reqObj.setBuyerCode("");
			if(reqObj.getDealerCode()==null)
				reqObj.setDealerCode("");
			if(reqObj.getSerialNumber()==null)
				reqObj.setSerialNumber("");
			 if(reqObj.getTransferDate()==null)
				 reqObj.setTransferDate("");
			 
			String messageString = reqObj.getSellerCode()+"|"+reqObj.getBuyerCode()+"|"+reqObj.getDealerCode()+"|"+
									reqObj.getSerialNumber()+"|"+reqObj.getTransferDate();
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			/*errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"AssetSaleFromD2Cservice WSDL not available");*/
			errorHandler.handleErrorMessages_new(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"AssetSaleFromD2Cservice WSDL not available","0002","Service Invokation");
			
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
