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
import remote.wise.service.datacontract.UserAlertPreferenceReqContract;
import remote.wise.service.datacontract.UserAlertPreferenceRespContract;
import remote.wise.service.implementation.UserAlertPreferenceImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;


@WebService
public class UserAlertPreferenceService {
		
	@WebMethod(operationName = "GetUserAlertPreference", action = "GetUserAlertPreference")
	public List<UserAlertPreferenceRespContract> getUserAlertPreference(UserAlertPreferenceReqContract reqObj ) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UserAlertPreferenceService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<-----Webservice Input----->");
		iLogger.info("LoginId:"+reqObj.getLoginId()+" , EventTypeId:"+reqObj.getEventTypeId()+" , RoleName:"+reqObj.getRoleName()+" ");
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil utilObj = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}

		//DF20181011 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=utilObj.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("UserAlertPreferenceService :: getUserAlertPreference ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		//DF20181005-KO369761-XSS validation of input req contract
		String isValidinput=null;

		isValidinput = utilObj.inputFieldValidation(reqObj.getRoleName());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(String.valueOf(reqObj.getEventTypeId()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());
				
		List<UserAlertPreferenceRespContract> respObj = new UserAlertPreferenceImpl().getUserAlertPreference(reqObj);
		iLogger.info("<-----Webservice Output----->");
		for(int i=0;i<respObj.size();i++){
			iLogger.info(i+"   ROW");
			iLogger.info("EventId:"+respObj.get(i).getEventId()+" , EventName:"+respObj.get(i).getEventName()+" , EventTypeId:"+respObj.get(i).getEventTypeId()+" , EventTypeName:"+respObj.get(i).getEventTypeName()+" ,"
					+ " LoginId:"+respObj.get(i).getLoginId()+" , RoleName:"+respObj.get(i).getRoleName()+ "isEmailEvent:"+respObj.get(i).isEmailEvent()+" , isSMSEvent:"+respObj.get(i).isSMSEvent()+"");
			
			//DF20181005-KO369761-XSS validation of output resp contract
			isValidinput = utilObj.responseValidation(respObj.get(i).getEventName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(respObj.get(i).getEventTypeName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(respObj.get(i).getEventTypeId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(respObj.get(i).isEmailEvent()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(respObj.get(i).isSMSEvent()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(respObj.get(i).getRoleName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
		}
		
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserAlertPreferenceService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		return respObj;
	}

	@WebMethod(operationName = "SetUserAlertPreference", action = "SetUserAlertPreference")
	public String setUserAlertPreference(@WebParam(name="reqObj")List<UserAlertPreferenceRespContract>reqObjList )throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UserAlertPreferenceService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<-----Webservice Input----->");
		
		//DF20181015 - KO369761 - Extracting CSRF Token & login id from edited by field.
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;
		CommonUtil utilObj = new CommonUtil();

		if(reqObjList.size() > 0 ){
			UserAlertPreferenceRespContract respObj = reqObjList.get(0);
			if(respObj.getLoginId().split("\\|").length > 1){
				loginId = respObj.getLoginId().split("\\|")[0];
				csrfToken = respObj.getLoginId().split("\\|")[1];
			}
		}
		
		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=utilObj.validateANTICSRFTOKEN(loginId,csrfToken);
		if(!isValidCSRF){
			iLogger.info("setUserAlertPreference ::  Invalid request.");
			throw new CustomFault("Invalid request.");

		}else{
			//delete the validated token
			utilObj.deleteANTICSRFTOKENS(loginId,csrfToken,"one");
		}
		
		//DF20181005-KO369761-XSS validation of input req contract
		String isValidinput=null;
		for(int i=0;i<reqObjList.size();i++){
			iLogger.info(i+"   ROW");
			iLogger.info("EventId:"+reqObjList.get(i).getEventId()+" , EventName:"+reqObjList.get(i).getEventName()+" , EventTypeId:"+reqObjList.get(i).getEventTypeId()+" , EventTypeName:"+reqObjList.get(i).getEventTypeName()+" ,"
					+ " LoginId:"+reqObjList.get(i).getLoginId()+" , RoleName:"+reqObjList.get(i).getRoleName()+  "isEmailEvent:"+reqObjList.get(i).isEmailEvent()+" , isSMSEvent:"+reqObjList.get(i).isSMSEvent()+"");
			
			if(reqObjList.get(i).getLoginId().split("\\|").length > 1){
				reqObjList.get(i).setLoginId(reqObjList.get(i).getLoginId().split("\\|")[0]);
			}
			
			//DF20181005-KO369761-XSS validation of output resp contract
			isValidinput = utilObj.responseValidation(reqObjList.get(i).getEventName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(reqObjList.get(i).getEventTypeName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObjList.get(i).getEventTypeId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = String.valueOf(reqObjList.get(i).isEmailEvent());
			if(!isValidinput.equals("true") && !isValidinput.equals("false")){
				throw new CustomFault(isValidinput);
			}

			isValidinput = String.valueOf(reqObjList.get(i).isSMSEvent());
			if(!isValidinput.equals("true") && !isValidinput.equals("false")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = utilObj.responseValidation(reqObjList.get(i).getRoleName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			//DF20170919 @Roopa getting decoded UserId
			String UserID=new CommonUtil().getUserId(reqObjList.get(i).getLoginId());
			reqObjList.get(i).setLoginId(UserID);
			iLogger.info("Decoded userId::"+reqObjList.get(i).getLoginId());
		}
		String response = new UserAlertPreferenceImpl().setUserAlertPreference(reqObjList);	    
		iLogger.info("<-----Webservice Output----->");
		iLogger.info("status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserAlertPreferenceService~executionTime:"+(endTime-startTime)+"~"+loginId+"~"+response);
		return response;	    
	}

	@WebMethod(operationName = "SetAdminPreference", action = "SetAdminPreference")
	public String setAdminAlertPreference(@WebParam(name="reqObj")List<UserAlertPreferenceRespContract>reqObjList )throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UserAlertPreferenceService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<-----Webservice Input----->");
		//DF20181005-KO369761-XSS validation of input req contract
		CommonUtil utilObj = new CommonUtil();
		
		//DF20181015 - KO369761 - Extracting CSRF Token & login id from edited by field.
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;

		if(reqObjList.size() > 0 ){
			UserAlertPreferenceRespContract respObj = reqObjList.get(0);
			if(respObj.getLoginId().split("\\|").length > 1){
				loginId = respObj.getLoginId().split("\\|")[0];
				csrfToken = respObj.getLoginId().split("\\|")[1];
			}
		}

		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=utilObj.validateANTICSRFTOKEN(loginId,csrfToken);
		if(!isValidCSRF){
			iLogger.info("setUserAlertPreference ::  Invalid request.");
			throw new CustomFault("Invalid request.");

		}else{
			//delete the validated token
			utilObj.deleteANTICSRFTOKENS(loginId,csrfToken,"one");
		}
		
		String isValidinput=null;
		for(int i=0;i<reqObjList.size();i++){
			iLogger.info(i+"   ROW");
			iLogger.info("EventId:"+reqObjList.get(i).getEventId()+" , EventName:"+reqObjList.get(i).getEventName()+" , EventTypeId:"+reqObjList.get(i).getEventTypeId()+" , EventTypeName:"+reqObjList.get(i).getEventTypeName()+" ,"
					+ " LoginId:"+reqObjList.get(i).getLoginId()+" , RoleName:"+reqObjList.get(i).getRoleName()+ "isEmailEvent:"+reqObjList.get(i).isEmailEvent()+" , isSMSEvent:"+reqObjList.get(i).isSMSEvent()+"");
			
			if(reqObjList.get(i).getLoginId().split("\\|").length > 1){
				reqObjList.get(i).setLoginId(reqObjList.get(i).getLoginId().split("\\|")[0]);
			}
			
			//DF20181005-KO369761-XSS validation of output resp contract
			isValidinput = utilObj.responseValidation(reqObjList.get(i).getEventName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(reqObjList.get(i).getEventTypeName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObjList.get(i).getEventTypeId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = String.valueOf(reqObjList.get(i).isEmailEvent());
			if(!isValidinput.equals("true") && !isValidinput.equals("false")){
				throw new CustomFault(isValidinput);
			}

			isValidinput = String.valueOf(reqObjList.get(i).isSMSEvent());
			if(!isValidinput.equals("true") && !isValidinput.equals("false")){
				throw new CustomFault(isValidinput);
			}
			
			isValidinput = utilObj.responseValidation(reqObjList.get(i).getRoleName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			
			//DF20170919 @Roopa getting decoded UserId
			String UserID=new CommonUtil().getUserId(reqObjList.get(i).getLoginId());
			reqObjList.get(i).setLoginId(UserID);
			iLogger.info("Decoded userId::"+reqObjList.get(i).getLoginId());
		}
		String response = new UserAlertPreferenceImpl().setAdminAlertPreference(reqObjList);	    
		iLogger.info("<-----Webservice Output----->");
		iLogger.info("status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserAlertPreferenceService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;	    
	}
}



