package remote.wise.EAintegration.EAclient;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.clientPackage.CustomerInformationService.CustomerInformationService;
import remote.wise.EAintegration.clientPackage.CustomerInformationService.CustomerInformationServiceService;
import remote.wise.EAintegration.dataContract.CustomerInfoInputContract;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;


public class CustomerInfoClient 
{
	public String invokeCustomerInfoService(CustomerInfoInputContract reqObj) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String status="SUCCESS";
		int wsdlError=0;
		CustomerInformationServiceService service1 =null;
		CustomerInformationService port1=null;
		
		try
		{
		
			//creating WebService Client
			iLogger.info(reqObj.getCustomerName()+" : Get the wsdl location");
			service1 = new CustomerInformationServiceService();
			
			//Create WebService
			 iLogger.info(reqObj.getCustomerName()+" : Get the webservice port");
			port1 = service1.getCustomerInformationServicePort();
			
			//Call webservice method
			iLogger.info(reqObj.getCustomerName()+" :Invoke webservice");
			iLogger.info("customerCode:"+reqObj.getCustomerCode()+",  "+"customerName:"+reqObj.getCustomerName() + ",  "+"zipCode:"+reqObj.getZipCode() +
					",  "+"addressLine1:"+reqObj.getAddressLine1()+",  "+"addressLine2:"+reqObj.getAddressLine2()+",  "+"city:"+reqObj.getCity()+",  "+"state"+reqObj.getState() +
					",  "+"zone:"+reqObj.getZone()+",  "+"country:"+reqObj.getCountry()+",  "+"email:"+reqObj.getEmail()+",  "+"contactNumber:"+reqObj.getContactNumber() +
					",  "+"fax:"+reqObj.getFax()+",  "+"primaryDealerCode:"+reqObj.getPrimaryDealerCode()+
					",  "+"Message Id:"+reqObj.getMessageId()+",  "+"File Reference:"+reqObj.getFileRef()+
					",  "+"Process:"+reqObj.getProcess()+",  "+"Reprocess JobCode:"+reqObj.getReprocessJobCode());
			
			
			status = port1.setCustomerDetails(reqObj.getCustomerCode(), reqObj.getCustomerName(), reqObj.getAddressLine1(), 
													reqObj.getAddressLine2(), reqObj.getCity(), reqObj.getZipCode(), reqObj.getState(), 
													reqObj.getZone(), reqObj.getCountry(), reqObj.getEmail(), reqObj.getContactNumber(), 
													reqObj.getFax(), reqObj.getPrimaryDealerCode(), reqObj.getMessageId(), reqObj.getFileRef(), 
													reqObj.getProcess(), reqObj.getReprocessJobCode());
			iLogger.info(reqObj.getCustomerName()+" :Returned from webservice, Status: "+status);
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available catch the error and take necessary actions */
		catch(Exception e)
		{
			wsdlError=1;
			
			status="FAILURE-CustomerInformationService:WSDL not available";
			fLogger.fatal(reqObj.getCustomerName()+"CustomerInformationService :WSDL not available");
		}
		
		/* DF20150806 - Rajani Nagaraju - If the wsdl is not available, store the details in Faultdetails table 
		 * so that the record will be reprocessed automatically. Otherwise, since the file is already moved to archived, we will have no trace of records
		 * that are not processed
		 */
		if(wsdlError==1 || port1==null || service1==null)
		{
			if( reqObj.getCustomerCode() == null || reqObj.getCustomerCode().equals("") || (!(reqObj.getCustomerCode().replaceAll("\\s","").length()>0)) )
				reqObj.setCustomerCode("");
			if( reqObj.getCustomerName() == null || reqObj.getCustomerName().equals("") || (!(reqObj.getCustomerName().replaceAll("\\s","").length()>0)) )
				reqObj.setCustomerName("");
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
			if( reqObj.getPrimaryDealerCode() == null || reqObj.getPrimaryDealerCode().equals("") || (!(reqObj.getPrimaryDealerCode().replaceAll("\\s","").length()>0)) )
				reqObj.setPrimaryDealerCode("");
			
			String messageString = reqObj.getCustomerCode()+"|"+reqObj.getCustomerName()+"|"+reqObj.getAddressLine1()+"|"+reqObj.getAddressLine2()+"|"
			          +reqObj.getCity()+"|"+reqObj.getZipCode()+"|"+reqObj.getState()+"|" +	reqObj.getZone()+"|"+reqObj.getCountry()+"|"+
			          reqObj.getEmail()+"|"+reqObj.getContactNumber()+"|"+reqObj.getFax()+"|"+reqObj.getPrimaryDealerCode();
			
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(reqObj.getMessageId(), messageString, reqObj.getFileRef(), reqObj.getProcess(), 
					reqObj.getReprocessJobCode(),"CustomerInformationService WSDL not available");
		}
		
		return status;
	}
}
