package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ClearAlertServiceImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "ClearAlertAfter24HoursService")
public class ClearAlertAfter24HoursService {
	
	
	
	@WebMethod(operationName = "ClearAlertsAfter24Hours", action = "ClearAlertsAfter24Hours")	
	public String clearAlertsAfter24Hours(){
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ClearAlertAfter24HoursService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("----Webservice Input----");
		String response= new ClearAlertServiceImpl().clearAlertAfter24Hrs();
		iLogger.info("----Webservice Output----");
		iLogger.info("status:"+response+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ClearAlertAfter24HoursService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;		
	}
}
