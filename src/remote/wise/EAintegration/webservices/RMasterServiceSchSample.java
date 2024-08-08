package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RMasterServiceSchedule;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RMasterServiceSchSample")
public class RMasterServiceSchSample 
{
	@WebMethod(operationName = "reprocessEAMasterServSch", action = "reprocessEAMasterServSch")
	  public String reprocessEAMasterServSch(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Master Service Schedule");
		RMasterServiceSchedule reprocessMasterSerSchObj = new RMasterServiceSchedule();
		reprocessMasterSerSchObj.reprocessMasterServiceSch();
		
		iLogger.info("Exiting Sample WebService - Reprocess Master Service Schedule");
		return "SUCCESS";
	}
}
