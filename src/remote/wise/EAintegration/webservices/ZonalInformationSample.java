package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.ZonalInformation;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "ZonalInformationSample")
public class ZonalInformationSample 
{
	@WebMethod(operationName = "processZonalInformation", action = "processZonalInformation")
	  public String processZonalInformation(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Zonal Information");
		ZonalInformation zonalInformation = new ZonalInformation();
		zonalInformation.handleZonalInformation();
		
		iLogger.info("Exiting Sample WebService  - Zonal Information");
		return "SUCCESS";
	}
}