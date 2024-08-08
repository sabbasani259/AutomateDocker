package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AccountDetailsImpl;
//import remote.wise.util.WiseLogger;

/** Webservice to obtain the Customer information details from EA system
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "CustomerInformationService")
public class CustomerInformationService 
{
	
	
	@WebMethod(operationName = "setCustomerDetails", action = "setCustomerDetails")
	public String setCustomerDetails(@WebParam(name="customerCode" ) String customerCode, 
			 			  @WebParam(name="customerName" ) String customerName,
			 			  @WebParam(name="addressLine1" ) String addressLine1,
			 			  @WebParam(name="addressLine2" ) String addressLine2,
			 			  @WebParam(name="city" ) String city,
			 			  @WebParam(name="zipCode" ) String zipCode,
			 			  @WebParam(name="state" ) String state,
			 			  @WebParam(name="zone" ) String zone,
			 			  @WebParam(name="country" ) String country,
			 			  @WebParam(name="email" ) String email,
			 			  @WebParam(name="contactNumber" ) String contactNumber,
			 			  @WebParam(name="fax" ) String fax,
			 			  @WebParam(name = "primaryDealerCode") String primaryDealerCode,
			 			  @WebParam(name="messageId" ) String messageId,
			 			  @WebParam(name="fileRef" ) String fileRef,
			 			  @WebParam(name="process" ) String process,
			 			  @WebParam(name="reprocessJobCode" ) String reprocessJobCode)
	{
		String response = "SUCCESS-Record Processed";
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("CustomerInformationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("EA Processing: CustomerInfo: "+messageId+": ---- Actual Webservice Input ------");
		iLogger.info("EA Processing: CustomerInfo: "+messageId+": customerCode:"+customerCode+",  "+"customerName:"+customerName + ",  "+"zipCode:"+zipCode +
				",  "+"addressLine1:"+addressLine1+",  "+"addressLine2:"+addressLine2+",  "+"city:"+city+",  "+"state"+state +
				",  "+"zone:"+zone+",  "+"country:"+country+",  "+"email:"+email+",  "+"contactNumber:"+contactNumber +
				",  "+"fax:"+fax+",  "+"primaryDealerCode:"+primaryDealerCode +
				",  "+"Message Id:"+messageId+",  "+"File Reference:"+fileRef+
				",  "+"Process:"+process+",  "+"Reprocess JobCode:"+reprocessJobCode);
		
		long startTime = System.currentTimeMillis();
		response = new AccountDetailsImpl().setCustomerAccountDetails(customerCode, customerName, addressLine1, addressLine2, city, zipCode, 
																		state, zone, country, email, contactNumber, fax, primaryDealerCode, messageId);
		long endTime = System.currentTimeMillis();
		iLogger.info("EA Processing: CustomerInfo: "+messageId+": Webservice Execution Time in ms:"+(endTime-startTime));
		
		iLogger.info("EA Processing: CustomerInfo: "+messageId+": ----- Actual Webservice Output-----");
		iLogger.info("EA Processing: CustomerInfo: "+messageId+": Status:"+response);
		
		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
		String faultCause =null;
		if(response.split("-").length>1)
		{
			faultCause=response.split("-")[1];
			response = response.split("-")[0].trim();
					
		}
		
		//If Failure in insertion, put the message to fault_details table
		if(response.equalsIgnoreCase("FAILURE"))
		{
			if( customerCode == null || customerCode.equals("") || (!(customerCode.replaceAll("\\s","").length()>0)) )
				customerCode="";
			if( customerName == null || customerName.equals("") || (!(customerName.replaceAll("\\s","").length()>0)) )
				customerName="";
			if( addressLine1 == null || addressLine1.equals("") || (!(addressLine1.replaceAll("\\s","").length()>0)) )
				addressLine1="";
			if( addressLine2 == null || addressLine2.equals("") || (!(addressLine2.replaceAll("\\s","").length()>0)) )
				addressLine2="";
			if( city == null || city.equals("") || (!(city.replaceAll("\\s","").length()>0)) )
				city="";
			if( zipCode == null || zipCode.equals("") || (!(zipCode.replaceAll("\\s","").length()>0)) )
				zipCode="";
			if( state == null || state.equals("") || (!(state.replaceAll("\\s","").length()>0)) ) 
				state="";
			if( zone == null || zone.equals("") || (!(zone.replaceAll("\\s","").length()>0)) )
				zone="";
			if( country == null || country.equals("") || (!(country.replaceAll("\\s","").length()>0)) )
				country="";
			if( email == null || email.equals("") || (!(email.replaceAll("\\s","").length()>0)) )
				email="";
			if( contactNumber == null || contactNumber.equals("") || (!(contactNumber.replaceAll("\\s","").length()>0)) )
				contactNumber="";
			if( fax == null || fax.equals("") || (!(fax.replaceAll("\\s","").length()>0)) )
				fax="";
			if( primaryDealerCode == null || primaryDealerCode.equals("") || (!(primaryDealerCode.replaceAll("\\s","").length()>0)) )
				primaryDealerCode="";
			
			String messageString = customerCode+"|"+customerName+"|"+addressLine1+"|"+addressLine2+"|"+city+"|"+zipCode+"|"+state+"|" +
									zone+"|"+country+"|"+email+"|"+contactNumber+"|"+fax+"|"+primaryDealerCode;
			
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
		}
		
		return response;
		
	}
}
