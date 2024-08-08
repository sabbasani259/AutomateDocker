package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AccountDetailsImpl;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;

/** Webservice to obtain the dealer information details from EA system
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "DealerInformationService")
public class DealerInformationService 
{
	@WebMethod(operationName = "setDealerDetails", action = "setDealerDetails")
	public String setDealerDetails(@WebParam(name="dealerCode" ) String dealerCode, 
			 			  @WebParam(name="dealerName" ) String dealerName,
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
			 			  @WebParam(name="JcbRoCode" ) String JcbRoCode,
			 			  @WebParam(name="messageId" ) String messageId,
			 			  @WebParam(name="fileRef" ) String fileRef,
			 			  @WebParam(name="process" ) String process,
			 			  @WebParam(name="reprocessJobCode" ) String reprocessJobCode)
	{
		String response = "SUCCESS-Record Processed";

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("DealerInformationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("EA Processing: DealerInfo: "+messageId+": ---- Actual Webservice Input ------");
		iLogger.info("EA Processing: DealerInfo: "+messageId+": DealerCode:"+dealerCode+",  "+"dealerName:"+dealerName + ",  "+"zipCode:"+zipCode +
				",  "+"addressLine1:"+addressLine1+",  "+"addressLine2:"+addressLine2+",  "+"city:"+city+",  "+"state"+state +
				",  "+"zone:"+zone+",  "+"country:"+country+",  "+"email:"+email+",  "+"contactNumber:"+contactNumber +
				",  "+"fax:"+fax+",  "+"JcbRoCode:"+JcbRoCode +
				",  "+"Message Id:"+messageId+",  "+"File Reference:"+fileRef+
				",  "+"Process:"+process+",  "+"Reprocess JobCode:"+reprocessJobCode);
		
		long startTime = System.currentTimeMillis();
		response = new AccountDetailsImpl().setDealerAccountDetails(dealerCode, dealerName, addressLine1, addressLine2, city, zipCode, 
																		state, zone, country, email, contactNumber, fax, JcbRoCode, messageId);
		long endTime = System.currentTimeMillis();
		iLogger.info("EA Processing: DealerInfo: "+messageId+": Webservice Execution Time in ms:"+(endTime-startTime));
		iLogger.info("serviceName:DealerInformationService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		iLogger.info("EA Processing: DealerInfo: "+messageId+": ----- Actual Webservice Output-----");
		iLogger.info("EA Processing: DealerInfo: "+messageId+": Status:"+response);
		
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
			if( dealerCode == null || dealerCode.equals("") || (!(dealerCode.replaceAll("\\s","").length()>0)) )
				dealerCode="";
			if( dealerName == null || dealerName.equals("") || (!(dealerName.replaceAll("\\s","").length()>0)) )
				dealerName="";
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
			if( JcbRoCode == null || JcbRoCode.equals("") || (!(JcbRoCode.replaceAll("\\s","").length()>0)) )
				JcbRoCode="";
			
			String messageString = dealerCode+"|"+dealerName+"|"+addressLine1+"|"+addressLine2+"|"+city+"|"+zipCode+"|"+state+"|" +
									zone+"|"+country+"|"+email+"|"+contactNumber+"|"+fax+"|"+JcbRoCode;
			
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, reprocessJobCode,faultCause,"0003","Service Layer");

			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);;
			}
		}else{
			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "sucessCount", 1);
			iLogger.info("Status on updating data into interface log details table :"+uStatus);
			if(messageId!=null && messageId.split("\\|").length>1){
				CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", -1);
			}
			//DF20180207:KO369761 - deleting message from fault details table if it is there.
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			messageHandlerObj.deleteErrorMessage(messageId);
		}
		
		return response;
		
	}
}
