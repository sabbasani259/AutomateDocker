package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ResetPasswordReqContract;
import remote.wise.service.implementation.ResetPasswordImpl;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;

@WebService(name = "ResetPasswordService")
public class ResetPasswordService {
		
	@WebMethod(operationName = "ResetPassword", action = "ResetPassword")	
	public String resetPassword(@WebParam(name="reqObj") ResetPasswordReqContract reqObj)throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ResetPasswordService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginID:"+reqObj.getLoginID()+","+"newPassword:"+reqObj.getNewPassword()+"");
		
		//validating login_id text
		//DF20171011: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();

		String isUserValid = util.inputFieldValidation(reqObj.getNewPassword());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginID());
		reqObj.setLoginID(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginID());
				
				
		String message = new ResetPasswordImpl().resetPassword(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+message+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ResetPasswordService~executionTime:"+(endTime-startTime)+"~"+reqObj.getLoginID()+"~"+message);
		
		
		
		return message;
	}

}
