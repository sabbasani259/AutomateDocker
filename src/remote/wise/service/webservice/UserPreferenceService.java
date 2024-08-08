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
import remote.wise.service.datacontract.UserPreferenceReqContract;
import remote.wise.service.datacontract.UserPreferenceRespContract;
import remote.wise.service.implementation.UserPreferenceImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
/**
 * 
 * @author tejgm
 * The service gets the UserPreference
 */
@WebService(name = "UserPreferenceService")
public class UserPreferenceService {
		
	/**
	 * 
	 * @param UserPreferenceReqContract is passed to getb the details on the UserPreference
	 * @return respObj when the response is got based on the request passed
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetUserPreference", action = "GetUserPreference")
	public List<UserPreferenceRespContract> getUserPreference(@WebParam(name="UserPreferenceReqContract")UserPreferenceReqContract UserPreferenceReqContract)throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UserPreferenceService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("<---- Webservice Input ------>");
		iLogger.info("contactId:"+UserPreferenceReqContract.getContact());
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(UserPreferenceReqContract.getContact());
				UserPreferenceReqContract.setContact(UserID);
				iLogger.info("Decoded userId::"+UserPreferenceReqContract.getContact());
				
				
		List<UserPreferenceRespContract> respObj=new UserPreferenceImpl().getUserPreference(UserPreferenceReqContract);
		iLogger.info("<---- Webservice Onput ------>");
		for(int i=0;i<respObj.size();i++){
			iLogger.info(i+"   ROW");
			iLogger.info("CatalogId:"+respObj.get(i).getCatalogId()+" , CatalogName:"+respObj.get(i).getCatalogName()+" ,  CatalogValue:"+respObj.get(i).getCatalogValue()+" , CatalogValueId:"+respObj.get(i).getCatalogValueId()+" , ContactId:"+respObj.get(i).getContactId()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserPreferenceService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}
	/**
	 * 
	 * @param userPreResp is passed as a input to set the values on the preference
	 * @return response that was set as a preference
	 * @throws CustomFault
	 */

	@WebMethod(operationName = "SetUserPreference", action = "SetUserPreference")

	public String setUserPreference(@WebParam(name="userPreResp")UserPreferenceRespContract userPreResp)throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("UserPreferenceService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(userPreResp.getContactId());
		userPreResp.setContactId(UserID);
		iLogger.info("Decoded userId::"+userPreResp.getContactId());
		
		
		String response=new UserPreferenceImpl().setUserPreference(userPreResp);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserPreferenceService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}

}
