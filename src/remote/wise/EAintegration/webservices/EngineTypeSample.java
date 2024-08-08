package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.EngineType;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "EngineTypeSample")
public class EngineTypeSample 
{
	@WebMethod(operationName = "processEAengineTypeData", action = "processEAengineTypeData")
	  public String processEAengineTypeData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Engine Type");
		EngineType engineTypeObj = new EngineType();
		engineTypeObj.handleEngineTypeDetails();
		
		iLogger.info("Exiting Sample WebService  - Engine Type");
		return "SUCCESS";
	}
}
