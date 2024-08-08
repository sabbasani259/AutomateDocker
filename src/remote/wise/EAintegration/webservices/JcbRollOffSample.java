package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.JCBRollOff;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "JcbRollOffSample")
public class JcbRollOffSample 
{
	@WebMethod(operationName = "processEARollOffdata", action = "processEARollOffdata")
	  public String processEARollOffdata(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - JCB Roll Off");
		JCBRollOff rollOffObj = new JCBRollOff();
		rollOffObj.handleJcbRollOffEAdata();
		
		iLogger.info("Exiting Sample WebService - JCB Roll Off");
		return "SUCCESS";
	}
}
