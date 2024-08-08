package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.sound.midi.MidiDevice.Info;

import org.apache.logging.log4j.Logger;



import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AdminAlertPrefReqContract;
import remote.wise.service.datacontract.AdminAlertPrefRespContract;
import remote.wise.service.implementation.AdminAlertPrefImpl;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;

/** WebService class to set and get the notification mode selected for each Alerts
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "AdminAlertPrefService")
public class AdminAlertPrefService
{
	
	
	/** This method gets the notification mode for all alerts OR for the specified alert/alert Type
	 * @param reqObj Get the details of specific Alert or Alert Type by passing the same to this request Object
	 * @return Returns the mode of notification selected for all alerts or the specified alert/alert type
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetAlertMode", action = "GetAlertMode")
	public List<AdminAlertPrefRespContract> getAlertMode(@WebParam(name="reqObj" ) AdminAlertPrefReqContract reqObj) throws CustomFault
	{   
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AdminAlertPrefService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate : "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("LoginId:"+reqObj.getLoginId()+",  "+"Event Name:"+reqObj.getEventName()+",  "+"Event Type Id:"+reqObj.getEventTypeId());
		
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
			iLogger.info("AdminAlertPrefService :: Invalid request.");
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
		
		isValidinput = utilObj.inputFieldValidation(reqObj.getEventName());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = utilObj.inputFieldValidation(String.valueOf(reqObj.getEventTypeId()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
				
				
		List<AdminAlertPrefRespContract> response = new AdminAlertPrefImpl().getEventMode(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("EventTypeId:"+response.get(i).getEventTypeId()+",  "+"EventTypeName:"+response.get(i).getEventTypeName()+",   " +
					"EventName:"+response.get(i).getEventName()+",  "+"Is SMS:"+response.get(i).isSMS()+",  "+"Is Email:"+response.get(i).isEmail());
			
			//DF20181005-KO369761-XSS validation of output resp contract
			isValidinput = utilObj.responseValidation(response.get(i).getEventName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(response.get(i).getEventTypeName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(response.get(i).getEventTypeId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(response.get(i).isEmail()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(response.get(i).isSMS()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate : "+endDate);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:AdminAlertPrefService~executionTime:"+(endTime - startTime)+"~"+""+"~");
		return response;

	}


	/** This method sets the notification mode for each Alert
	 * @param reqObj Get the notification modes to be set for each Alert
	 * @return Returns the status String as either SUCCESS/FAILURE for setting the notification modes for Alerts
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetAlertMode", action = "SetAlertMode")
	public String setAlertMode(@WebParam(name="reqObj" ) List<AdminAlertPrefRespContract> reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
	//	WiseLogger infoLogger = WiseLogger.getLogger("AdminAlertPrefService:","info");
		
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		Logger businessError = BusinessErrorLoggerClass.logger;

		//DF20181005-KO369761-XSS validation of input req contract
		CommonUtil utilObj = new CommonUtil();
		String isValidinput=null;

		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate : "+startDate);
		long startTime = System.currentTimeMillis();
		infoLogger.info("----- Webservice Input-----");
		for(int i=0; i<reqObj.size(); i++)
		{
			infoLogger.info("EventTypeId:"+reqObj.get(i).getEventTypeId()+",  "+"EventTypeName:"+reqObj.get(i).getEventTypeName()+",   " +
					"EventName:"+reqObj.get(i).getEventName()+",  "+"Is SMS:"+reqObj.get(i).isSMS()+",  "+"Is Email:"+reqObj.get(i).isEmail());
			
			isValidinput = utilObj.responseValidation(reqObj.get(i).getEventName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(reqObj.get(i).getEventTypeName());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObj.get(i).getEventTypeId()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObj.get(i).isEmail()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			isValidinput = utilObj.responseValidation(String.valueOf(reqObj.get(i).isSMS()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		String response = new AdminAlertPrefImpl().setEventMode(reqObj);
		infoLogger.info("----- Webservice Output-----");
		infoLogger.info("Status:"+response+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate : "+endDate);
		long endTime = System.currentTimeMillis();
		infoLogger.info("serviceName:AdminAlertPrefService~executionTime:"+(endTime - startTime)+"~"+""+"~"+response);
		return response;

	}

}
