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
import remote.wise.service.datacontract.AlertThresholdReqContract;
import remote.wise.service.datacontract.AlertThresholdRespContract;
import remote.wise.service.implementation.AlertThresholdImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

@WebService(name = "AlertThresholdService")
public class AlertThresholdSettingsService 
{

	

	@WebMethod(operationName="GetAlertThreshold", action = "GetAlertThreshold")
	public List<AlertThresholdRespContract> getAlertThresholdSettings(@WebParam(name="reqObject") AlertThresholdReqContract reqObj) throws CustomFault{
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AlertThresholdSettingsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Serial Number:"+reqObj.getLoginId()+"");
		
		//DF20181015 - KO369761 - Extracting CSRF Token from login field.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		if(reqObj.getLoginId().split("\\|").length > 1){
			csrfToken=reqObj.getLoginId().split("\\|")[1];
			reqObj.setLoginId(reqObj.getLoginId().split("\\|")[0]);
		}

		//DF20181015 - KO369761 - Validating the CSRF Token against login id.
		if(csrfToken != null)
			isValidCSRF=util.validateANTICSRFTOKEN(reqObj.getLoginId(),csrfToken);
		if(!isValidCSRF){
			iLogger.info("AlertThresholdSettings ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		
		if(UserID == null){
			throw new CustomFault("Invalid Login ID");
		}
		
		//DF20181005-KO369761-XSS validation of input req contract
		CommonUtil utilObj = new CommonUtil();
		String isValidinput=null;
				
		List<AlertThresholdRespContract> respObj = new AlertThresholdImpl().getAlertThresholdSettings(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++)
		{
			iLogger.info("EventTypeId:"+respObj.get(i).getEventTypeId()+",  "+"eventTypeName:"+respObj.get(i).getEventTypeName()+",   " +
					"EventId:"+respObj.get(i).getEventId()+",  "+"isYellowThreshold:"+respObj.get(i).isYellowThreshold()+",  "+"isRedThreshold:"+respObj.get(i).isRedThreshold()+", "+" YellowThresholdVal:"+respObj.get(i).getYellowThresholdVal()+","+"RedThresholdVal:"+respObj.get(i).getRedThresholdVal()+","+"EventName:"+respObj.get(i).getEventName()+"");
			
			//DF20181005-KO369761-XSS validation of output response contract
			isValidinput = utilObj.responseValidation(respObj.get(i).getEventName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(respObj.get(i).getEventTypeName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(respObj.get(i).getEventId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(respObj.get(i).getEventTypeId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(respObj.get(i).isRedThreshold()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(respObj.get(i).getRedThresholdVal()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(respObj.get(i).getYellowThresholdVal()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(respObj.get(i).isYellowThreshold()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AlertThresholdService~executionTime:"+(endTime - startTime)+"~"+""+"~");
		return respObj;
	}

	@WebMethod(operationName = "SetAlertThreshold", action = "SetAlertThreshold")
	public String setAlertThresholdSettings(@WebParam(name="reqObj" )List<AlertThresholdRespContract> reqObjList )throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AlertThresholdSettingsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		CommonUtil utilObj = new CommonUtil();
		String isValidinput=null;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("----Webservice Input----");
		for(int i=0; i<reqObjList.size(); i++)
		{
			iLogger.info("EventTypeId:"+reqObjList.get(i).getEventTypeId()+",  "+"eventTypeName:"+reqObjList.get(i).getEventTypeName()+",   " +
					"EventId:"+reqObjList.get(i).getEventId()+",  "+"isYellowThreshold:"+reqObjList.get(i).isYellowThreshold()+",  "+"isRedThreshold:"+reqObjList.get(i).isRedThreshold()+", "+" YellowThresholdVal:"+reqObjList.get(i).getYellowThresholdVal()+","+"RedThresholdVal:"+reqObjList.get(i).getRedThresholdVal()+","+"EventName:"+reqObjList.get(i).getEventName()+"");
			
			//DF20181005-KO369761-XSS validation of output response contract
			isValidinput = utilObj.responseValidation(reqObjList.get(i).getEventName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(reqObjList.get(i).getEventTypeName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObjList.get(i).getEventId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObjList.get(i).getEventTypeId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObjList.get(i).isRedThreshold()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObjList.get(i).getRedThresholdVal()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObjList.get(i).getYellowThresholdVal()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObjList.get(i).isYellowThreshold()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}

		String response = new AlertThresholdImpl().setAlertThresholdSettings(reqObjList);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AlertThresholdService~executionTime:"+(endTime - startTime)+"~"+""+"~"+response);
		return response;
	}
}
