package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AlertDetailsReqContract;
import remote.wise.service.datacontract.AlertDetailsRespContract;
import remote.wise.service.implementation.AlertDetailsImpl;
//import remote.wise.util.WiseLogger;

/** WebService class to get the details of a notification
 * @author Rajani Nagaraju
 * 
 */
@WebService(name = "AlertDetailsService")
public class AlertDetailsService 
{
	
	/** This method gets the details of an Alert
	 * @param reqObj specifies the VIN and alertId whose details has to be returned
	 * @return Details of the specified alert is returned
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetAlertDetails", action = "GetAlertDetails")
	public AlertDetailsRespContract getAlertDetails(@WebParam(name="reqObj" ) AlertDetailsReqContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AlertDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSSS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate : "+startDate);
		
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Serial Number:"+reqObj.getSerialNumber()+",  "+"Asset Event Id:"+reqObj.getAssetEventId());
		AlertDetailsRespContract response = new AlertDetailsImpl().getAlertDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Serial Number:"+response.getSerialNumber()+",  "+"Asset Event Id:"+response.getAssetEventId()+",   " +
				"Life Hours:"+response.getLifeHours()+",  "+"Latitude:"+response.getLatitude()+",  "+"Longitude:"+response.getLongitude()+",  " +
				"Notes: "+response.getNotes());
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSSS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate : "+endDate);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:AlertDetailsService~executionTime:"+(endTime - startTime)+"~"+""+"~");
		return response;

	}


	/** This method sets the comment for an alert
	 * @param reqObj Alert Comment
	 * @return Status string of updating alert comment
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "setAlertComments", action = "setAlertComments")
	public String setAlertComments(@WebParam(name="reqObj" ) AlertDetailsRespContract reqObj) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AlertDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate : "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Serial Number:"+reqObj.getSerialNumber()+",  "+"Asset Event Id:"+reqObj.getAssetEventId()+",   " +
				"Life Hours:"+reqObj.getLifeHours()+",  "+"Latitude:"+reqObj.getLatitude()+",  "+"Longitude:"+reqObj.getLongitude()+",  " +
				"Notes: "+reqObj.getNotes());
		String response = new AlertDetailsImpl().setAlertComments(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate : "+endDate);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:AlertDetailsService~executionTime:"+(endTime - startTime)+"~"+""+"~"+response);
		return response;

	}

}
