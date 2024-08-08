package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.XsdParsingImpl;
//import remote.wise.util.WiseLogger;

/** WebService to parser the XSD
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "XsdParsingService")
public class XsdParsingService 
{
	/** Webmethod that parse the XSD file using DOM parser
	 * @param confirmParsing confirmation String
	 * @return Status of parsing
	 */
	@WebMethod(operationName = "ParseXSD", action = "ParseXSD")
	public String parseXSD(@WebParam(name="confirmParsing" ) boolean confirmParsing) 
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("XsdParsingService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("confirmParsing:"+confirmParsing);
		String responseStatus = "FAILURE";
		if(confirmParsing==true)
		{
			XsdParsingImpl parsingImplObj = new XsdParsingImpl();
			responseStatus = parsingImplObj.parserXSD();
		}
		iLogger.info("---- Webservice Output ------");
		iLogger.info("Status:"+responseStatus);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:XsdParsingService~executionTime:"+(endTime-startTime)+"~"+""+"~"+responseStatus);
		return responseStatus;
	}
}
