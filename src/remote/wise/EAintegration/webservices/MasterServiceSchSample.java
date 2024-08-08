package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.MasterServiceSchedule;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "MasterServiceSchSample")
public class MasterServiceSchSample 
{
	@WebMethod(operationName = "processEAMasterServSch", action = "processEAMasterServSch")
	  public String processEAMasterServSch(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Master Service Schedule");
		MasterServiceSchedule masterSerSchObj = new MasterServiceSchedule();
		masterSerSchObj.handleMasterServiceSchedule();
		
		iLogger.info("Exiting Sample WebService  -  Master Service Schedule");
		return "SUCCESS";
	}
}
