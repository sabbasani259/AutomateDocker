package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RServiceHistory;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RServiceHistorySample")
public class RServiceHistorySample 
{
	@WebMethod(operationName = "reprocessEAServiceHistory", action = "reprocessEAServiceHistory")
	  public String reprocessEAServiceHistory(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Service History");
		RServiceHistory reprocessServiceHistObj = new RServiceHistory();
		reprocessServiceHistObj.reprocessServiceHistory();
		
		iLogger.info("Exiting Sample WebService - Service History");
		return "SUCCESS";
	}
}
