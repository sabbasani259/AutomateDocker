package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RAssetGateOut;
import remote.wise.EAintegration.handler.RZonalInformation;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RAssetGateOutSample")
public class RZonalInformationSample 
{
	@WebMethod(operationName = "reprocessEAzonalInformation", action = "reprocessEAzonalInformation")
	  public String reprocessEAzonalInformation(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Zonal Information");
		RZonalInformation reprocessZonalInfo = new RZonalInformation();
		reprocessZonalInfo.reprocessZonalInformation();
		
		iLogger.info("Exiting Sample WebService - Reprocess Zonal Information");
		return "SUCCESS";
	}
}
