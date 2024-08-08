package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.LandmarkFactDataImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "LandmarkFactDataService")
public class LandmarkFactDataService {
	
	/**
	 * Service to set data in LandmarkFactEntity_DayAgg table for a current day.
	 * @return String
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "setLandmarkFactData", action = "http://webservice.service.wise.remote/setLandmarkFactData")
	public String setLandmarkFactData() throws CustomFault {

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("LandmarkFactDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("----- Webservice Input-----");
		String response_msg = new LandmarkFactDataImpl().setLandmarkFactData();
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response_msg+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:LandmarkFactDataService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response_msg);
		return response_msg;
	}
}
