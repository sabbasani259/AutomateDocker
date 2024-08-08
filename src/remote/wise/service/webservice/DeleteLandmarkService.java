package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.DeleteLandmarkRespContract;
import remote.wise.service.implementation.DeleteLandmarkImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;

/**
 * DeleteLandmarkService will allow to delete LandmarkCategory.
 *  @author jgupta41
 */
@WebService(name = "DeleteLandmarkService")
public class DeleteLandmarkService {
	
	
	/**
	 * This method will change status to zero for specified Landmark Id 
	 * @param RespObj Delete Landmark for a given Landmark 
	 * @return response_msg  Returns the status String as either SUCCESS/FAILURE for setting activeStatus for given Landmark
	 * @throws CustomFault  custom exception is thrown when the user login id is not specified or invalid,Landmark Id  is invalid when specified
	 */
	@WebMethod(operationName = "SetDeleteLandmark", action = "SetDeleteLandmark")
	public String setDeleteLandmark(@WebParam(name="RespObj" )DeleteLandmarkRespContract RespObj) throws CustomFault

	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("DeleteLandmarkService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("----- Webservice Input-----");
		
		//DF20181008 - XSS validation of input for Security Fixes.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(String.valueOf(RespObj.getLandmark_id()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		String response_msg= new DeleteLandmarkImpl().setDeleteLandmark(RespObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response_msg);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:DeleteLandmarkService~executionTime:"+(endTime - startTime)+"~"+""+"~"+response_msg);
		return response_msg;
	}
}
