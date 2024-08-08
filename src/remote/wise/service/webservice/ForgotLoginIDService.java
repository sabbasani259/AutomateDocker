/**
 * 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ForgotLoginIDReqContract;
import remote.wise.service.datacontract.ForgotLoginIDRespContract;
import remote.wise.service.implementation.ForgotLoginIDImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 * @author kprabhu5
 *
 */
@WebService(name = "ForgotLoginIDService")
public class ForgotLoginIDService {
	
	
	@WebMethod(operationName = "ForgotLoginID", action = "ForgotLoginID")	
	public ForgotLoginIDRespContract getForgottenLoginID(@WebParam(name="reqObj") ForgotLoginIDReqContract reqObj)throws CustomFault{
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ForgotLoginIDService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("emailID:"+reqObj.getEmailID()+","+"mobileNumber:"+reqObj.getMobileNumber()+","+"questionId1:"+reqObj.getQuestionId1()+","+"answer1:"+reqObj.getAnswer1()+","+"questionId2:"+reqObj.getQuestionId2()+","+"answer2:"+reqObj.getAnswer2()+"");

		//DF20171011: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		
		String isUserValid = util.inputFieldValidation(reqObj.getEmailID());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		
		isUserValid = util.inputFieldValidation(reqObj.getMobileNumber());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		
		isUserValid = util.inputFieldValidation(reqObj.getAnswer1());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		
		isUserValid = util.inputFieldValidation(reqObj.getAnswer2());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}
		
		ForgotLoginIDRespContract response= new ForgotLoginIDImpl().getForgottenLoginId(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("primaryEmailID:"+response.getPrimaryEmailID()+","+"message:"+response.getMessage()+","+"loginID:"+response.getMessage()+","+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ForgotLoginIDService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;

	}

}
