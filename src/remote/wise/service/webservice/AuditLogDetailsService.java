package remote.wise.service.webservice;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import remote.wise.service.datacontract.AuditLogReqContract;
import remote.wise.service.datacontract.AuditLogRespContract;

import remote.wise.service.implementation.AuditLogDetailsImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
/**
 *  WebService class to get Audit Log Details for given Tenancy 
 * @author jgupta41
 *
 */

@WebService(name = "AuditLogDetailsService")
public class AuditLogDetailsService {
	
	
	/**
	 *  This method gets Audit Log Details for given Tenancy Id
	 * @param reqObj:Get Audit Log Details for given Tenancy Id
	 * @return respObj:Returns List of Audit Log Details 
	 * @throws CustomFault
	 */
	
	@WebMethod(operationName="GetAuditLogDetails", action = "GetAuditLogDetails")
	public List<AuditLogRespContract> getAuditLogDetails(@WebParam(name="reqObj") AuditLogReqContract reqObj) throws CustomFault{
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AuditLogDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		String loginId = null;
		String csrfToken = null;
		boolean isValidCSRF = false;
		CommonUtil util = new CommonUtil();
		
		//DF20181011 - KO369761 - Validating the CSRF Token against login id.
		if(reqObj.getFromDate().split("\\|").length > 2){
			loginId = reqObj.getFromDate().split("\\|")[1];
			//DF20190430 I Anudeep Adding loggers to check how inputs are coming from UI
			iLogger.info("FromDate: "+reqObj.getFromDate() + "ToDate" + reqObj.getToDate());
			csrfToken =  reqObj.getFromDate().split("\\|")[2];
			reqObj.setFromDate(reqObj.getFromDate().split("\\|")[0]);
		}
		if(csrfToken!=null){
			isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
		}
		iLogger.info("getAuditLogDetails ::   csrftoken isValidCSRF :: "+isValidCSRF);
		if(!isValidCSRF)
		{
			iLogger.info("getAuditLogDetails ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}

		//DF20181008-KO369761-XSS validation of input request contract
		String isValidinput=null;
		//DF20190430 I Anudeep Adding loggers to check how inputs are coming from UI
		iLogger.info("Input field: From Date " +reqObj.getFromDate());
		isValidinput = util.inputFieldValidation(reqObj.getFromDate());
		if(!isValidinput.equals("SUCCESS")){
			iLogger.info("Input field: From Date " +reqObj.getFromDate()+ " validation has failed");
			throw new CustomFault(isValidinput);
		}
		iLogger.info("Input field: To Date " +reqObj.getToDate());
		isValidinput = util.inputFieldValidation(reqObj.getToDate());
		if(!isValidinput.equals("SUCCESS")){
			iLogger.info("Input Field ToDate " +reqObj.getToDate()+ " validation has failed");
			throw new CustomFault(isValidinput);
		}
		iLogger.info("Input Field Tenancy ID " +String.valueOf(reqObj.getTenancyId()));
		isValidinput = util.inputFieldValidation(String.valueOf(reqObj.getTenancyId()));
		if(!isValidinput.equals("SUCCESS")){
			iLogger.info("Input Field Tenancy ID " +String.valueOf(reqObj.getTenancyId())+ " validation has failed");
			throw new CustomFault(isValidinput);
		}
		
		List<AuditLogRespContract> respObj = new AuditLogDetailsImpl().getAuditLogDetails(reqObj);
		
		for(int i = 0;i<respObj.size();i++){
			isValidinput = util.inputFieldValidation(respObj.get(i).getLastLoginDate());
			if(!isValidinput.equals("SUCCESS")){
				iLogger.info("Index " + i + "Last Login Date " +respObj.get(i).getLastLoginDate()+ " validation has failed");
				throw new CustomFault(isValidinput);
			}

			isValidinput = util.inputFieldValidation(respObj.get(i).getUserId());
			if(!isValidinput.equals("SUCCESS")){
				iLogger.info("Index " + i + "Last Login Date " +respObj.get(i).getLastLoginDate()+ " validation has failed");
				throw new CustomFault(isValidinput);
			}

			isValidinput = util.inputFieldValidation(respObj.get(i).getUserName());
			if(!isValidinput.equals("SUCCESS")){
				iLogger.info("Index " + i + "Last Login Date " +respObj.get(i).getLastLoginDate()+ " validation has failed");
				throw new CustomFault(isValidinput);
			}
		}
		
		iLogger.info("----- Webservice Output-----");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AuditLogDetailsService~executionTime:"+(endTime-startTime)+"~"+loginId+"~");
		return respObj;
	}

}
