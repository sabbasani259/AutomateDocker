package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.REngineType;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "REngineTypeSample")
public class REngineTypeSample 
{
	@WebMethod(operationName = "reprocessEAengineTypeData", action = "reprocessEAengineTypeData")
	  public String reprocessEAengineTypeData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Engine Type");
		REngineType reprocessEngineTypeObj = new REngineType();
		reprocessEngineTypeObj.reprocessEngineType();
		
		iLogger.info("Exiting Sample WebService - Reprocess Engine Type");
		return "SUCCESS";
	}
}
