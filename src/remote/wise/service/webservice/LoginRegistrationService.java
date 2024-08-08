package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LoginRegistrationRespContract;
import remote.wise.service.datacontract.SecretQuestionsRespContract;
import remote.wise.service.implementation.LoginRegistrationImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "LoginRegistrationService")
public class LoginRegistrationService {
		
	/**
	 * 
	 * method to get secret questions for new user login first time
	 */
	@WebMethod(operationName = "GetSecretQuestions", action = "GetSecretQuestions")
	public List<SecretQuestionsRespContract> getSecretQuestions()
	{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("LoginRegistrationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		List<SecretQuestionsRespContract> response= new LoginRegistrationImpl().getSecretQuestions();
		iLogger.info("---- Webservice Output ------");
		for(int i=0; i<response.size(); i++){
			iLogger.info("QID:"+response.get(i).getQID()+","+"question:"+response.get(i).getQuestion()+","+"message:"+response.get(i).getMessage()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:LoginRegistrationService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}

	/**
	 * 
	 * method to set values for secret questions for first time login -I
	 */
	@WebMethod(operationName = "SetSecretQuestionsToUser", action = "SetSecretQuestionsToUser")
	public String setSecretQuestionsToUser(@WebParam(name="reqObj") LoginRegistrationRespContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("LoginRegistrationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("loginId:"+reqObj.getLoginId()+","+"questionId1:"+reqObj.getQuestionId1()+","+"questionId2:"+reqObj.getQuestionId2()+","+"question1:"+reqObj.getQuestion1()+","+"question2:"+reqObj.getQuestion2()+","+"answer1:"+reqObj.getAnswer1()+","+"answer2:"+reqObj.getAnswer2()+","+"DOB:"+reqObj.getDOB()+","+"nativeState:"+reqObj.getNativeState()+"");
		
		//DF20171011: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();

		String isUserValid = util.inputFieldValidation(reqObj.getAnswer1());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		isUserValid = util.inputFieldValidation(reqObj.getAnswer2());
		if(!isUserValid.equals("SUCCESS")){
			throw new CustomFault(isUserValid);
		}

		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
		
		String message= new LoginRegistrationImpl().setSecretQuestions(reqObj);
		iLogger.info("---- Webservice Output ------");
		iLogger.info("Status:"+message+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:LoginRegistrationService~executionTime:"+(endTime-startTime)+"~"+UserID+"~"+message);
		return message;
	}

	/**
	 * method to get user selected secret questions -IV
	 */
	@WebMethod(operationName = "GetQuestionsToUser", action = "GetQuestionsToUser")
	public List<SecretQuestionsRespContract> getUsersSecretQuestions(@WebParam(name="contactId") String contactId) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("LoginRegistrationService:","info");
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		infoLogger.info("---- Webservice Input ------");
		infoLogger.info("ContactId:"+contactId+"");
		
		//DF20170919 @Roopa getting decoded UserId
		contactId=new CommonUtil().getUserId(contactId);
				
		infoLogger.info("Decoded userId::"+contactId);
				
		List<SecretQuestionsRespContract> secretQsList= new LoginRegistrationImpl().getSecretQuestionsForUser(contactId);
		infoLogger.info("---- Webservice Output ------");
		for(int i=0; i<secretQsList.size(); i++){
			infoLogger.info("QID:"+secretQsList.get(i).getQID()+","+"question:"+secretQsList.get(i).getQuestion()+","+"message:"+secretQsList.get(i).getMessage()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		infoLogger.info("serviceName:LoginRegistrationService~executionTime:"+(endTime-startTime)+"~"+contactId+"~");
		return secretQsList;
	}

}
