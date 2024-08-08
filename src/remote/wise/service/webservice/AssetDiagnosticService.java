package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import org.apache.logging.log4j.Logger;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetDiagnosticImpl;
import remote.wise.util.StaticProperties;
//import remote.wise.util.WiseLogger;

/** WebService to handle asset diagnostic data received from device
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "AssetDiagnosticService")
public class AssetDiagnosticService 
{
	/**This method sets the diagnostic data received from the device 
	 * @param xmlInput Diagnostic details received as XML input
	 * @return Returns the Status String
	 */

	@WebMethod(operationName = "SetMonitoringData", action = "SetMonitoringData")
	public String setAssetMonitoringData(@WebParam(name="xmlInput" ) String xmlInput) 
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetDiagnosticService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		
		String responseStatus = "FAILURE";
		
		//Logger infoLogger = Logger.getLogger("infoLogger");
				
		//infoLogger.info("---- Webservice Input ------");
		//infoLogger.info("xmlInput:"+xmlInput);
		
		//Get SerialNumber and Transaction time to maintain the Logger format
		String serialNumber = StringUtils.substringBetween(xmlInput, "<SerialNumber>", "</SerialNumber>").substring(3);
		String transactionTime = StringUtils.substringBetween(xmlInput, "SnapshotTime=\"", "\"").replace("T", " ").replace("Z", "");
		
		Calendar cal = Calendar.getInstance();
		String startDate = StaticProperties.dateTimeSecFormat.format(cal.getTime());
		long startTime = System.currentTimeMillis();
		iLogger.info(serialNumber+":"+transactionTime+":"+" ADS Processing StartTime: "+startDate);
		
		responseStatus = new AssetDiagnosticImpl().setAssetMonitoringDataNew(xmlInput);
		
		Calendar cal1 = Calendar.getInstance();
		String endDate = StaticProperties.dateTimeSecFormat.format(cal1.getTime());
		long endTime=System.currentTimeMillis();
		iLogger.info(serialNumber+":"+transactionTime+":"+" ADS Processing EndTime: "+endDate);
		iLogger.info(serialNumber+":"+transactionTime+":"+"Webservice Execution Time in ms:"+(endTime-startTime));
		iLogger.info("serviceName:AssetDiagnosticService~executionTime:"+(endTime-startTime)+"~"+""+"~"+responseStatus);
		
		return responseStatus;

	}

}
