package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.PendingTenancyCreationReqContract;
import remote.wise.service.datacontract.PendingTenancyCreationRespContract;
import remote.wise.service.implementation.PendingTenancyCreationImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;


/** Webservice that returns the List of child accounts that are pending for tenancy creation
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "PendingTenancyCreationService")
public class PendingTenancyCreationService 
{
	/** Webmethod that returns the list of Accounts for which the tenancy is not yet created
	 * @param reqObj
	 * @return
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetPendingTenancyCreation", action = "GetPendingTenancyCreation")
	public List<PendingTenancyCreationRespContract> getPendingTenancyCreation(@WebParam(name="reqObj" ) PendingTenancyCreationReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("PendingTenancyCreationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+reqObj.getLoginId()+",  "+"tenancyIdList:"+reqObj.getTenancyIdList());
		
		CommonUtil util = new CommonUtil();
		//DF20181025 -  - Extracting CSRF Token from login field.
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}
		//DF20181025 -  - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("PendingTenancyCreationService :: GetPendingTenancyCreation ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		//DF20181008-KO369761-XSS validation of input request contract
		String isValidinput=null;
		ListToStringConversion convObj = new ListToStringConversion();
		String childTenancyList = convObj.getIntegerListString(reqObj.getTenancyIdList()).toString();
		isValidinput = util.inputFieldValidation(childTenancyList);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		List<PendingTenancyCreationRespContract> response = new PendingTenancyCreationImpl().getPendingAccounts(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("accountId: "+response.get(i).getAccountId()+",  "+"accountName:"+response.get(i).getAccountName()+",   " +
					"parentTenancyId:"+response.get(i).getParentTenancyId()+",  "+"parentTenancyName:"+response.get(i).getParentTenancyName());

			isValidinput = util.inputFieldValidation(response.get(i).getAccountName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(response.get(i).getParentTenancyName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(response.get(i).getAccountId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = util.inputFieldValidation(String.valueOf(response.get(i).getParentTenancyId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:PendingTenancyCreationService~executionTime:"+(endTime-startTime)+"~"+reqObj.getLoginId()+"~");
		return response;
	}

}
