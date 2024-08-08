package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RAllFailures;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "DailyReprocessSample")
public class DailyReprocessSample 
{

	@WebMethod(operationName = "reprocessAllFailedRecords", action = "reprocessAllFailedRecords")
	  public String reprocessAllFailedRecords(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess All Failed Records");
		RAllFailures reprocessFailureObj = new RAllFailures();
		reprocessFailureObj.reprocessAllFailureRecords();
		
		iLogger.info("Entering Sample WebService - Reprocess All Failed Records");
		return "SUCCESS";
	}
}
