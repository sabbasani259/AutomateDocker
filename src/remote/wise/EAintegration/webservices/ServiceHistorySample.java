package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.ServiceHistory;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "ServiceHistorySample")
public class ServiceHistorySample 
{
	@WebMethod(operationName = "processEAServiceHistory", action = "processEAServiceHistory")
	  public String processEAServiceHistory(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Service History");
		ServiceHistory serviceHistoryObj = new ServiceHistory();
		serviceHistoryObj.handleServiceHistoryDetails();
		
		iLogger.info("Exiting Sample WebService  - Service History");
		return "SUCCESS";
	}
}
