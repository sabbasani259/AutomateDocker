package remote.wise.EAintegration.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.handler.RAssetPersonality;
import remote.wise.log.InfoLogging.InfoLoggerClass;

@WebService(name = "RAssetPersonalitySample")
public class RAssetPersonalitySample 
{
	@WebMethod(operationName = "reprocessEAassetPersData", action = "reprocessEAassetPersData")
	  public String reprocessEAassetPersData(@WebParam(name="msg" ) String msg) 
	{
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entering Sample WebService - Reprocess Asset Personality");
		RAssetPersonality reprocessAssetPersObj = new RAssetPersonality();
		reprocessAssetPersObj.reprocessAssetPersonality();
		
		iLogger.info("Exiting Sample WebService - Reprocess Asset Personality");
		return "SUCCESS";
	}
}
