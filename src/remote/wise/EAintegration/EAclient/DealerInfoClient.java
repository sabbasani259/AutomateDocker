package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.DealerInformationService.DealerInformationService;
import remote.wise.EAintegration.clientPackage.DealerInformationService.DealerInformationServiceService;
import remote.wise.EAintegration.dataContract.DealerInfoInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class DealerInfoClient 
{
	public String invokeDealerInfoService(DealerInfoInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		DealerInformationServiceService service1=null;
		DealerInformationService port1=null;
		
		try
		{
			//creating WebService Client
			iLogger.info(reqObj.getDealerName()+" : Get the wsdl location");
			service1 = new DealerInformationServiceService();
			
			//Create WebService
			iLogger.info(reqObj.getDealerName()+" : Get the webservice port");
			port1 = service1.getDealerInformationServicePort();
			
			//Call webservice method
			iLogger.info(reqObj.getDealerName()+" :Invoke webservice");
			iLogger.info("dealerCode:"+reqObj.getDealerCode()+",  "+"dealerName:"+reqObj.getDealerName() + ",  "+"zipCode:"+reqObj.getZipCode() +
					",  "+"addressLine1:"+reqObj.getAddressLine1()+",  "+"addressLine2:"+reqObj.getAddressLine2()+",  "+"city:"+reqObj.getCity()+",  "+"state"+reqObj.getState() +
					",  "+"zone:"+reqObj.getZone()+",  "+"country:"+reqObj.getCountry()+",  "+"email:"+reqObj.getEmail()+",  "+"contactNumber:"+reqObj.getContactNumber() +
					",  "+"fax:"+reqObj.getFax()+",  "+"JcbRoCode:"+reqObj.getJcbRoCode() +
					",  "+"Message Id:"+reqObj.getMessageId()+",  "+"File Reference:"+reqObj.getFileRef()+
					",  "+"Process:"+reqObj.getProcess()+",  "+"Reprocess JobCode:"+reqObj.getReprocessJobCode());
			
			
			status = port1.setDealerDetails(reqObj.getDealerCode(), reqObj.getDealerName(), reqObj.getAddressLine1(), 
													reqObj.getAddressLine2(), reqObj.getCity(), reqObj.getZipCode(), reqObj.getState(), 
													reqObj.getZone(), reqObj.getCountry(), reqObj.getEmail(), reqObj.getContactNumber(), 
													reqObj.getFax(), reqObj.getJcbRoCode(), reqObj.getMessageId(), reqObj.getFileRef(), 
													reqObj.getProcess(), reqObj.getReprocessJobCode());
			iLogger.info(reqObj.getDealerName()+" :Returned from webservice, Status: "+status);
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-DealerInformationService:WSDL not available";
			fLogger.fatal(reqObj.getDealerName()+"DealerInformationService:WSDL not available");
			
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{

			if( reqObj.getDealerCode() == null || reqObj.getDealerCode().equals("") || (!(reqObj.getDealerCode().replaceAll("\\s","").length()>0)) )
				reqObj.setDealerCode("");
			if( reqObj.getDealerName() == null || reqObj.getDealerName().equals("") || (!(reqObj.getDealerName().replaceAll("\\s","").length()>0)) )
				reqObj.setDealerName("");
			if( reqObj.getAddressLine1() == null || reqObj.getAddressLine1().equals("") || (!(reqObj.getAddressLine1().replaceAll("\\s","").length()>0)) )
				reqObj.setAddressLine1("");
			if( reqObj.getAddressLine2() == null || reqObj.getAddressLine2().equals("") || (!(reqObj.getAddressLine2().replaceAll("\\s","").length()>0)) )
				reqObj.setAddressLine2("");
			if( reqObj.getCity() == null || reqObj.getCity().equals("") || (!(reqObj.getCity().replaceAll("\\s","").length()>0)) )
				reqObj.setCity("");
			if( reqObj.getZipCode() == null || reqObj.getZipCode().equals("") || (!(reqObj.getZipCode().replaceAll("\\s","").length()>0)) )
				reqObj.setZipCode("");
			if( reqObj.getState() == null || reqObj.getState().equals("") || (!(reqObj.getState().replaceAll("\\s","").length()>0)) ) 
				reqObj.setState("");
			if( reqObj.getZone() == null || reqObj.getZone().equals("") || (!(reqObj.getZone().replaceAll("\\s","").length()>0)) )
				reqObj.setZone("");
			if( reqObj.getCountry() == null || reqObj.getCountry().equals("") || (!(reqObj.getCountry().replaceAll("\\s","").length()>0)) )
				reqObj.setCountry("");
			if( reqObj.getEmail() == null || reqObj.getEmail().equals("") || (!(reqObj.getEmail().replaceAll("\\s","").length()>0)) )
				reqObj.setEmail("");
			if( reqObj.getContactNumber() == null || reqObj.getContactNumber().equals("") || (!(reqObj.getContactNumber().replaceAll("\\s","").length()>0)) )
				reqObj.setContactNumber("");
			if( reqObj.getFax() == null || reqObj.getFax().equals("") || (!(reqObj.getFax().replaceAll("\\s","").length()>0)) )
				reqObj.setFax("");
			if( reqObj.getJcbRoCode() == null || reqObj.getJcbRoCode().equals("") || (!(reqObj.getJcbRoCode().replaceAll("\\s","").length()>0)) )
				reqObj.setJcbRoCode("");
			
			String messageString = reqObj.getDealerCode()+"|"+reqObj.getDealerName()+"|"+reqObj.getAddressLine1()+"|"+
								reqObj.getAddressLine2()+"|"+reqObj.getCity()+"|"+reqObj.getZipCode()+"|"+reqObj.getState()+"|" +
								reqObj.getZone()+"|"+reqObj.getCountry()+"|"+reqObj.getEmail()+"|"+reqObj.getContactNumber()+"|"+
								reqObj.getFax()+"|"+reqObj.getJcbRoCode();
			
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			/*errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"DealerInformationService WSDL not available");*/
			errorHandler.handleErrorMessages_new(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"DealerInformationService WSDL not available","0002","Service Invokation");

			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(reqObj.getMessageId().split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(reqObj.getFileRef(), "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);;
			}
		}else{
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
