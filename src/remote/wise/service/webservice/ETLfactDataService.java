package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ETLfactDataImpl;
import remote.wise.service.implementation.ETLfactDataJsonImpl;
//import remote.wise.util.WiseLogger;
/**
 * This Service will allow to insert data in RemoteMonitoringFactDataDayAgg table for a current day.
 * @author jgupta41
 *
 */
@WebService(name = "ETLfactDataService")
public class ETLfactDataService {
	
	/**
	 * This method will allow to set data in RemoteMonitoringFactDataDayAgg table for a current day.
	 * @return response_msg
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetETLfactData", action = "http://webservice.service.wise.remote/SetETLfactData")
	  public String setETLfactData()  throws CustomFault

	  {
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ETLfactDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();
		
		//String response_msg= new ETLfactDataImpl().setETLfactData();
		
		//DF20170428 @Roopa Redirecting to new ETL json service
		
		String response_msg= new ETLfactDataJsonImpl().setETLfactJsonDetails();
		
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:ETLfactDataService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		iLogger.info("----- Webservice Output-----");
		return response_msg;
	  }
	/**
	 * This method will update AssetClassId in All fact tables
	 * @return SUCCESS
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "updateRemoteFact", action = "http://webservice.service.wise.remote/updateRemoteFact")
	  public String updateRemoteFact()  throws CustomFault

	  {
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ETLfactDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();
		
		String response_msg= new ETLfactDataImpl().updateRemoteFact();
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:ETLfactDataService~executionTime:"+(endTime-startTime)+"~"+""+"~");
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
		//WiseLogger infoLogger = WiseLogger.getLogger("ETLfactDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();
		
		String response_msg= new ETLfactDataImpl().updateTenancyIdInFactTables();
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:ETLfactDataService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		iLogger.info("----- Webservice Output-----");
		return response_msg;
	  }
	
	/**
	 * This method will update Address in All fact tables
	 * @return SUCCESS
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "updateAddress", action = "http://webservice.service.wise.remote/updateAddress")
	public String updateAddress() throws CustomFault
	{ 
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("ETLfactDataService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();
		
		String response_msg= new ETLfactDataImpl().updateAddress(10000);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:ETLfactDataService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		iLogger.info("----- Webservice Output-----");
		return response_msg;
	  
	}
}
