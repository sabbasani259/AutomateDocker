package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ForgotPasswordReqContract;
import remote.wise.service.datacontract.ForgotPasswordRespContract;
import remote.wise.service.implementation.ForgotPasswordImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "ForgotPasswordService")
public class ForgotPasswordService {
	
	
	
	@WebMethod(operationName = "GetForgottenPassword", action = "GetForgottenPassword")	
	public ForgotPasswordRespContract getForgottenPassword(@WebParam(name="reqObj") ForgotPasswordReqContract reqObj)throws CustomFault{
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ForgotPasswordService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+reqObj.getLoginId()+"");
		
		//validating login_id text
		//DF20171011: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();

		String isUserValid = util.inputFieldValidation(reqObj.getLoginId());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		
		ForgotPasswordRespContract response= new ForgotPasswordImpl().getForgottenPassword(reqObj);
		iLogger.info("---- Webservice Output ------");
		iLogger.info("password:"+response.getPassword()+","+"primaryEmailID:"+response.getPrimaryEmailID()+","+"message:"+response.getMessage()+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ForgotPasswordService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}
}
