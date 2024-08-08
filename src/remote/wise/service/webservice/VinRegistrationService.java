package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetDiagnosticImpl;
//import remote.wise.util.WiseLogger;

/** WebService to handle VIN Registration
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "VinRegistrationService")
public class VinRegistrationService 
{	
	/** WebMethod that registers a VIN and sends back the acknowledgement
	 * @param xmlInput VIN,IMEI and SIM combination received as XML input
	 * @return Registration acknowledegement returned as XML String
	 */
	@WebMethod(operationName = "RegisterVIN", action = "RegisterVIN")
	public String registerVIN(@WebParam(name="xmlInput" ) String xmlInput) 
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("VinRegistrationService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		String[] msgString = xmlInput.split("\\|");
		
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info(msgString[0]+": "+msgString[1]+ " Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info(msgString[0]+": "+msgString[1]+" ---- Webservice Input ------");
		
		//DF20141107 - Rajani Nagaraju - START - Accepting registration data as "|" seperated String instead of XML
	//	iLogger.info("XML input: "+xmlInput);
		iLogger.info(msgString[0]+": "+msgString[1]+" VIN Registration input: "+xmlInput);
		//DF20141107 - Rajani Nagaraju - END - Accepting registration data as "|" seperated String instead of XML
		
		String responseXML = null;
		AssetDiagnosticImpl assetDiagnosticObj = new AssetDiagnosticImpl();
		try
		{
			responseXML = assetDiagnosticObj.registerVIN(xmlInput);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		iLogger.info(msgString[0]+": "+msgString[1]+" ----- Webservice Output-----");
		iLogger.info(msgString[0]+": "+msgString[1]+" XML output: "+responseXML);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info(msgString[0]+": "+msgString[1]+"Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info(msgString[0]+": "+msgString[1]+"Webservice Execution Time in ms:"+(endTime-startTime));
		iLogger.info("serviceName:VinRegistrationService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return responseXML;

	}
}
