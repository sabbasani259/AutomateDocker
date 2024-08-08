package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ETLfactDataImpl;
import remote.wise.service.implementation.NotificationFactDataImpl;
//import remote.wise.util.WiseLogger;
/**
 * This Service will allow to insert data in NotificationFactEntity_DayAgg table for a current day.
 * @author jgupta41
 *
 */
@WebService(name = "NotificationFactDataService")
public class NotificationFactDataService {
	
	/**
	 * This method will allow to set data in NotificationFactEntity_DayAgg table for a current day.
	 * @return response_msg
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "setNotificationFactData", action = "http://webservice.service.wise.remote/setNotificationFactData")
	  public String setNotificationFactData()  throws CustomFault

	  {
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("NotificationFactDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();
		
		String response_msg= new NotificationFactDataImpl().setNotificationFactData();
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:NotificationFactDataService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response_msg);
		iLogger.info("----- Webservice Output-----");
		return response_msg;
	  }
	/**
	 * This method will update AssetClassId in All fact tables
	 * @return SUCCESS
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "updateNotificationFact", action = "http://webservice.service.wise.remote/updateNotificationFact")
	  public String updateRemoteFact()  throws CustomFault

	  {
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("NotificationFactDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();
		
		String response_msg= new NotificationFactDataImpl().updateNotificationFact();
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:NotificationFactDataService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response_msg);
		iLogger.info("----- Webservice Output-----");
		return response_msg;
	  }
	
	/**
	 * This method will update AssetClassId in All fact tables
	 * @return SUCCESS
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "updateTenancyIdInFactTables", action = "http://webservice.service.wise.remote/updateTenancyIdInFactTables")
	  public String updateTenancyIdInFactTables()  throws CustomFault

	  {
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("NotificationFactDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();
		
		String response_msg= new NotificationFactDataImpl().updateTenancyIdInFactTables();
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:NotificationFactDataService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response_msg);
		iLogger.info("----- Webservice Output-----");
		return response_msg;
	  }
}
