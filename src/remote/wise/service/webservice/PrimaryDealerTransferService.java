package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.logging.log4j.Logger;
import com.wipro.mda.AssetOwnershipDetails;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.PrimaryDealerTransferImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/** WebService to handle Primary Dealer Transfer
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "PrimaryDealerTransferService")
public class PrimaryDealerTransferService 
{	
	/** WebMethod that sets the details of primary dealer transfer
	 * @param customerCode customerCode as String input
	 * @param dealerCode dealerCode as String input
	 * @param transferDate Date of transfer of primary dealer
	 * @return Returns status String
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "primaryDealerTransfer", action = "primaryDealerTransfer")
	public String primaryDealerTransfer(@WebParam(name="customerCode" ) String customerCode, 
			@WebParam(name="dealerCode" ) String dealerCode,
			@WebParam(name="transferDate" ) String transferDate,
			@WebParam(name="serialNumber") String serialNumber,
			@WebParam(name="messageId" ) String messageId,
			@WebParam(name="fileRef" ) String fileRef,
			@WebParam(name="process" ) String process,
			@WebParam(name="reprocessJobCode" ) String reprocessJobCode) throws CustomFault
	{
		String response = "SUCCESS-Record Processed";
		PrimaryDealerTransferImpl dealerTransferObj = new PrimaryDealerTransferImpl();

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("PrimaryDealerTransferService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("EA Processing: PrimaryDealerTransfer: "+messageId+": ---- Webservice Input ------");
		iLogger.info("EA Processing: PrimaryDealerTransfer: "+messageId+": dealerCode:"+dealerCode+",  "+"customerCode:"+customerCode +
				", "+"SerialNumber:"+serialNumber+",  "+"transferDate:"+transferDate);

		long startTime = System.currentTimeMillis();
		
		//DF20181016 KO369761 : CSRF Token Validation ---Start---.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;

		if(serialNumber !=null && !serialNumber.equalsIgnoreCase("null") ){
			if(serialNumber.split("\\|").length > 2){
				String[] vinNum = serialNumber.split("\\|");
				serialNumber = vinNum[0];
				loginId = vinNum[1];
				csrfToken =  vinNum[2];
				if(csrfToken!=null){
					isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
				}
				iLogger.info("PrimaryDealerTransfer :: PrimaryDealerTransfer ::   csrftoken isValidCSRF :: "+isValidCSRF);
				if(!isValidCSRF)
				{
					iLogger.info("PrimaryDealerTransfer :: PrimaryDealerTransfer ::  Invalid request.");
					throw new CustomFault("Invalid request.");
				}else{
					util.deleteANTICSRFTOKENS(loginId, csrfToken, "one");
				}
			}
		}
		
		//DF20181008 - XSS validation for Security Fixes.
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(customerCode);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(dealerCode);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(serialNumber);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(transferDate);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		response = dealerTransferObj.primaryDealerTransfer(customerCode, dealerCode, transferDate, serialNumber, messageId);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:PrimaryDealerTransferService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);

		iLogger.info("EA Processing: PrimaryDealerTransfer: "+messageId+": ----- Webservice Output-----");
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
			if(customerCode==null)
				customerCode=""; 
			if(dealerCode==null)
				dealerCode="";
			if(transferDate==null)
				transferDate="";
			if(serialNumber==null)
				serialNumber="";

			String messageString = customerCode+"|"+dealerCode+"|"+transferDate+"|"+serialNumber;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
		}else{
			//DF20180108: @SU334449 - Invoking MOOLDA Layer from WISE post successful persistence of machine ownership details in AOS table
			//new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber);//20220921.o.Additional log added
			new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber, "PrimaryDealerTransfer");//20220921.n.Additional log added
		}
		return response;
	}
}
