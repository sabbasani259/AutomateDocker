package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RJCBRollOff;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RJcbRollOffSample")
public class RJcbRollOffSample 
{
	@WebMethod(operationName = "reprocessEARollOffdata", action = "reprocessEARollOffdata")
	  public String reprocessEARollOffdata(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Roll Off");
		RJCBRollOff reprocessRollOffObj = new RJCBRollOff();
		reprocessRollOffObj.reprocessJCBRollOff();
		
		iLogger.info("Exiting Sample WebService - Reprocess Roll Off");
		return "SUCCESS";
	}
}
