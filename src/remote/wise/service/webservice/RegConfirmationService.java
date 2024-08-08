package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.RegConfirmationReqContract;
import remote.wise.service.datacontract.RegConfirmationRespContract;
import remote.wise.service.implementation.RegConfirmationImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/** Webservice class to handle RegConfirmationService
 * @author Suprava Nayak
 *
 */
@WebService(name = "RegConfirmationService")
public class RegConfirmationService {


	/** This method sets the details of IMEI Number and Phone Number
	 * @param reqObj details of Registration Confirmation through reqObj
	 * @return Returns the status message of new IMEI Number and phone Number set to the database
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetRegConfirmation", action = "SetRegConfirmation")
	public String setRegistrationConfirmation(@WebParam(name="reqObj" ) RegConfirmationRespContract reqObj) throws CustomFault
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("RegConfirmationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("IMEI_Number:"+reqObj.getIMEINumber()+","+"Phone_Number:"+reqObj.getPhoneNumber()+","+"flag:"+reqObj.isFlag()+","+"userID:"+reqObj.getUserID());
	
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getUserID());
		reqObj.setUserID(UserID);
		iLogger.info("Decoded userId::"+reqObj.getUserID());
		
		String response = new RegConfirmationImpl().setRegConfirmation(reqObj);
		iLogger.info("----- Webservice Output-----");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:RegConfirmationService~executionTime:"+(endTime-startTime)+"~"+UserID+"~"+response);
		return response;

	}


	/** This method returns the details of RegistrationConfirmation of the IMEI number
	 * @param reqObj IMEI number is specified for which details are to be returned
	 * @return details of required IMEI number
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetRegistrationConfirmation", action = "GetRegistrationConfirmation")
	public RegConfirmationRespContract getRegistrationConfirmation(@WebParam(name="reqObj" ) RegConfirmationReqContract reqObj) throws CustomFault
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("RegConfirmationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("IMEI_Number:"+reqObj.getIMEINumber());
		RegConfirmationRespContract response = new RegConfirmationImpl().getRegConfirmation(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("IMEI_Number:"+response.getIMEINumber()+", "+"Phone_Number:"+response.getPhoneNumber()+","+"user_ID:"+response.getUserID()+", "+"flag:"+response.isFlag());
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:RegConfirmationService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;

	}


	/**This method deletes the RegistrationConfirmation for IMEI number
	 * 	 * @param reqObj IMEI number that has to be deleted 
	 * @return status of RegistrationConfirmation deletion
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "DeleteRegistrationConfirmation", action = "DeleteRegistrationConfirmation")
	public String DeleteRegistrationConfirmation(@WebParam(name="reqObj" ) RegConfirmationRespContract reqObj) throws CustomFault
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("RegConfirmationService:","info");
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		infoLogger.info("---- Webservice Input ------");
		infoLogger.info("IMEI_Number:"+reqObj.getIMEINumber()+",  "+"Phone_Number:"+reqObj.getPhoneNumber()+","+"flag:"+reqObj.isFlag());
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getUserID());
				reqObj.setUserID(UserID);
				infoLogger.info("Decoded userId::"+reqObj.getUserID());
				
		String response = new RegConfirmationImpl().deleteRegConfirmation(reqObj);
		infoLogger.info("----- Webservice Output-----");
		infoLogger.info("Status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		infoLogger.info("serviceName:RegConfirmationService~executionTime:"+(endTime-startTime)+"~"+UserID+"~"+response);
		return response;

	}


}
